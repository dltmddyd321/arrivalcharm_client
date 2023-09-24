package com.example.arrivalcharm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.core.AppConst
import com.example.arrivalcharm.databinding.ActivityMainBinding
import com.example.arrivalcharm.datamodel.UserLoginInfo
import com.example.arrivalcharm.viewmodel.AdviceViewModel
import com.example.arrivalcharm.viewmodel.LoginViewModel
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
class MainActivity : AppCompatActivity() {

    @NetworkModule.Advice
    private val adviceViewModel: AdviceViewModel by viewModels()

    @NetworkModule.Main
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NaverIdLoginSDK.initialize(this, AppConst.NAVER_CLIENT_ID, AppConst.NAVER_CLIENT_SECRET, "ArrivalCharm")

        lifecycleScope.launch {
            adviceViewModel.fetchAdvice().collect { result ->
                when (result) {
                    is ApiResult.Success -> Toast.makeText(this@MainActivity, result.data, Toast.LENGTH_LONG).show()
                    is ApiResult.Error -> Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.naverLoginBtn.setOnClickListener {
            var naverToken :String? = ""

            val profileCallback = object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(result: NidProfileResponse) {
                    val userId = result.profile?.id
                    Timber.tag("네이버 로그인").i("id: $userId \ntoken: $naverToken")
                    Toast.makeText(this@MainActivity, "네이버 아이디 로그인 성공!", Toast.LENGTH_SHORT).show()

                    val token = naverToken ?: return
                    val naverId = result.profile?.id ?: return

                    val loginObject = UserLoginInfo(
                        token,
                        naverId,
                        "이승용",
                        "1q2w3e4r!",
                        "dltmddyd321@naver.com"
                    )
                    lifecycleScope.launch {
                        loginViewModel.startLogin(loginObject).collect { result ->
                            when (result) {
                                is ApiResult.Success -> Toast.makeText(this@MainActivity, result.data, Toast.LENGTH_LONG).show()
                                is ApiResult.Error -> Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Timber.tag("네이버 로그인").i("errorCode: ${errorCode}\n" +
                            "errorDescription: ${errorDescription}")
                    Toast.makeText(this@MainActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: $errorDescription", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@MainActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: $errorDescription", Toast.LENGTH_SHORT).show()
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }
    }
}