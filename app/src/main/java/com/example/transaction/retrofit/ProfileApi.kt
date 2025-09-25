import retrofit2.http.GET

interface ProfileApi {
    @GET("api/trackers/me")
    suspend fun getProfile(): ProfileResponse
}

data class ProfileResponse(
    val username: String,
    val firstname: String,
    val lastname: String
)
