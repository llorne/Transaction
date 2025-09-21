import com.example.transaction.retrofit.Token
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/auth/login")
    suspend fun authLogin(@Body loginRequest: LoginRequest): Token

    @POST("api/auth/refresh")
    suspend fun refresh(@Body refreshRequest: RefreshRequest): Token
}

