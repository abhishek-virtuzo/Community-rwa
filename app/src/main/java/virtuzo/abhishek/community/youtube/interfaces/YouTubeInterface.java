package virtuzo.abhishek.community.youtube.interfaces;

import virtuzo.abhishek.community.youtube.models.Videos;

import retrofit2.Call;
import retrofit2.http.GET;

public interface YouTubeInterface {

    @GET("youtube/v3/search?order=date&part=snippet&channelId=UC17norgrt9kUwYCvYkE0W8w&maxResults=25&key=AIzaSyDJyTHMpYs9iP77dLngUwFRVD1mxViId7k")
//    @GET("youtube/v3/search?order=date&part=snippet&channelId=UCJuV6LWXL97Nktznv8ysAQA&maxResults=25&key=AIzaSyDJyTHMpYs9iP77dLngUwFRVD1mxViId7k")
//  @GET("youtube/v3/videos?part=snippet%2CcontentDetails&&chart=mostPopular&key=AIzaSyD8IYPTWpp2d7FZGzc9QufkRjJm1rwodOk")
    Call<Videos> getVideos();

}