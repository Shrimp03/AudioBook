package com.example.audiobook.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.LoginDTO;
import com.example.audiobook.dto.response.UserLoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    public static MutableLiveData<String> toast = new MutableLiveData<>();
    private static final String TAG = "LoginViewModel";

    public MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    private final Context context;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public void loginUser(String email, String password) {
        LoginDTO loginDTO = new LoginDTO(email,password);
        CoreAppInterface.coreAppInterface.loginAccount(loginDTO).enqueue(new Callback<UserLoginResponse>() {
           @Override
           public void onResponse(@NonNull Call<UserLoginResponse> call, @NonNull Response<UserLoginResponse> response) {
               Log.d(TAG, "onResponse called."+ response);
               Log.d(TAG, "Response raw: " + response.toString());
               Log.d(TAG, "Response code: " + response.code());
               Log.d(TAG, "Response successful: " + response.isSuccessful());
               try{
                   if(response.isSuccessful()){
                       UserLoginResponse responseData = response.body();
                       Log.d(TAG, "Response body: " + responseData.toString());
                       String userToken = responseData.getData().getAccessToken();
                       if(responseData.getStatus().equals("ok")) {
                           if (userToken != null && !userToken.isEmpty()) {
                               saveToken(userToken);
                               Log.d(TAG, "Token saved successfully.");
                           } else {
                               Log.e(TAG, "Login successful, but token is null or empty.");
                           }
                           toast.setValue(responseData.getMessage());
                           registrationSuccess.setValue(true);
                       }
                   }
               }catch (Exception e){

               }
           }

           @Override
           public void onFailure(Call<UserLoginResponse> call, Throwable t) {
               toast.setValue("false");
               registrationSuccess.setValue(false);
           }
       });

    }
    private void saveToken(String token) {
        // Lấy đối tượng SharedPreferences.
        // PreferenceManager.getDefaultSharedPreferences lấy file preferences mặc định của ứng dụng.
        // Bạn cũng có thể dùng context.getSharedPreferences("MyAuthPrefs", Context.MODE_PRIVATE);
        // để tạo file preferences riêng.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Lấy Editor để chỉnh sửa SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();

        // Lưu token với một key cố định, ví dụ "auth_token"
        editor.putString("auth_token", token);

        // Áp dụng thay đổi. apply() làm việc bất đồng bộ, commit() làm việc đồng bộ.
        // apply() thường được ưu tiên vì không chặn luồng UI.
        editor.apply();
    }

    /**
     * Phương thức tùy chọn để lấy token đã lưu từ SharedPreferences.
     * Bạn có thể gọi phương thức này ở nơi khác (ví dụ: Activity/Fragment)
     * khi bạn cần sử dụng token cho các yêu cầu API khác.
     *
     * @return Token đã lưu, hoặc null nếu không tìm thấy hoặc chưa được lưu.
     */
    public String getSavedToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Trả về giá trị của key "auth_token", nếu không tồn tại thì trả về null
        return prefs.getString("auth_token", null);
    }

    /**
     * Phương thức tùy chọn để xóa token khi người dùng đăng xuất.
     */
    public void clearToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("auth_token");
        editor.apply();
    }
}