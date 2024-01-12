package com.example.arrivalcharm.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.core.AppConst
import com.example.arrivalcharm.databinding.ActivityLoginBinding
import com.example.arrivalcharm.datamodel.UserLoginInfo
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.type.LoginType
import com.example.arrivalcharm.viewmodel.LoginViewModel
import com.example.arrivalcharm.viewmodel.TokenRefreshViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @NetworkModule.Main
    private val loginViewModel: LoginViewModel by viewModels()

    private val dataStoreViewModel: DatastoreViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val userId = account.id ?: return@registerForActivityResult

                val loginObject = UserLoginInfo(
                    LoginType.GOOGLE.name.lowercase(),
                    userId,
                    account.email ?: "",
                    account.displayName ?: ""
                )
                lifecycleScope.launch {
                    loginViewModel.startLogin(loginObject).collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                val resultData = result.data
                                dataStoreViewModel.putUserName(resultData.name)
                                dataStoreViewModel.putAuthToken(resultData.accessToken)
                                dataStoreViewModel.putRefreshToken(resultData.refreshToken)
                                dataStoreViewModel.putAuthId(resultData.userId.toInt())
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            }
                            is ApiResult.Error -> Toast.makeText(
                                this@LoginActivity,
                                result.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.googleLoginBtn.setOnClickListener {
            requestGoogleLogin()
        }

        binding.naverLoginBtn.setOnClickListener {
            requestNaverLogin()
        }

        binding.kakaoLoginBtn.setOnClickListener {
            requestKakaoLogin()
        }
    }

    private fun requestKakaoLogin() {
        val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(this@LoginActivity, "카카오 아이디 로그인 실패!", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                UserApiClient.instance.me { user, err ->
                    if (err != null) return@me
                    Toast.makeText(this@LoginActivity, "카카오 아이디 로그인 성공! 1 -> ${user?.id}", Toast.LENGTH_SHORT).show()
                    val loginObject = UserLoginInfo(
                        LoginType.KAKAO.name.lowercase(),
                        user?.id.toString(),
                        user?.kakaoAccount?.email ?: "",
                        user?.kakaoAccount?.name ?: ""
                    )
                    lifecycleScope.launch {
                        loginViewModel.startLogin(loginObject).collect { result ->
                            when (result) {
                                is ApiResult.Success -> {
                                    val resultData = result.data
                                    dataStoreViewModel.putUserName(resultData.name)
                                    dataStoreViewModel.putAuthToken(resultData.accessToken)
                                    dataStoreViewModel.putRefreshToken(resultData.refreshToken)
                                    dataStoreViewModel.putAuthId(resultData.userId.toInt())
                                    Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                }
                                is ApiResult.Error -> Toast.makeText(
                                    this@LoginActivity,
                                    result.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Toast.makeText(this@LoginActivity, "카카오 아이디 로그인 실패!", Toast.LENGTH_SHORT).show()
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
                } else if (token != null) {
                    Toast.makeText(this@LoginActivity, "카카오 아이디 로그인 성공! 2 -> ${token.accessToken}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    private fun requestNaverLogin() {
        var naverToken: String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                val userId = result.profile?.id
                Timber.tag("네이버 로그인").i("id: $userId \ntoken: $naverToken")
                Toast.makeText(this@LoginActivity, "네이버 아이디 로그인 성공!", Toast.LENGTH_SHORT).show()
                val naverId = result.profile?.id ?: return

                val loginObject = UserLoginInfo(
                    LoginType.NAVER.name.lowercase(),
                    naverId,
                    result.profile?.email ?: "",
                    result.profile?.name ?: ""
                )
                lifecycleScope.launch {
                    loginViewModel.startLogin(loginObject).collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                val resultData = result.data
                                dataStoreViewModel.putUserName(resultData.name)
                                dataStoreViewModel.putAuthToken(resultData.accessToken)
                                dataStoreViewModel.putRefreshToken(resultData.refreshToken)
                                dataStoreViewModel.putAuthId(resultData.userId.toInt())
                                Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            }
                            is ApiResult.Error -> Toast.makeText(
                                this@LoginActivity,
                                result.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Timber.tag("네이버 로그인").i(
                    "errorCode: ${errorCode}\n" +
                            "errorDescription: ${errorDescription}"
                )
                Toast.makeText(
                    this@LoginActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: $errorDescription", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                naverToken = NaverIdLoginSDK.getAccessToken()
                NidOAuthLogin().callProfileApi(profileCallback)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    this@LoginActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: $errorDescription", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(AppConst.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }
}