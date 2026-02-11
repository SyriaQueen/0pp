package com.halaqat.attendance.network;

import com.halaqat.attendance.models.*;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    
    // ═══════════════════════════════════════════════════════════════
    // Authentication APIs
    // ═══════════════════════════════════════════════════════════════
    
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body Map<String, String> credentials);
    
    @POST("auth/change-password")
    Call<ApiResponse<Object>> changePassword(
            @Header("Authorization") String token, 
            @Body Map<String, String> passwords
    );
    
    // ═══════════════════════════════════════════════════════════════
    // Users APIs
    // ═══════════════════════════════════════════════════════════════
    
    @GET("users")
    Call<ApiResponse<List<User>>> getUsers(@Header("Authorization") String token);
    
    @GET("users/teachers")
    Call<ApiResponse<List<User>>> getTeachers(@Header("Authorization") String token);
    
    @GET("users/parents")
    Call<ApiResponse<List<User>>> getParents(@Header("Authorization") String token);
    
    @POST("users")
    Call<ApiResponse<User>> createUser(
            @Header("Authorization") String token, 
            @Body Map<String, String> user
    );
    
    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(
            @Header("Authorization") String token, 
            @Path("id") int userId, 
            @Body Map<String, String> user
    );
    
    @DELETE("users/{id}")
    Call<ApiResponse<Object>> deleteUser(
            @Header("Authorization") String token, 
            @Path("id") int userId
    );
    
    // ═══════════════════════════════════════════════════════════════
    // Students APIs
    // ═══════════════════════════════════════════════════════════════
    
    @GET("students")
    Call<ApiResponse<List<Student>>> getStudents(@Header("Authorization") String token);
    
    @GET("students/parent/{parentId}")
    Call<ApiResponse<List<Student>>> getStudentsByParent(
            @Header("Authorization") String token, 
            @Path("parentId") int parentId
    );
    
    @GET("students/fawj/{fawjId}")
    Call<ApiResponse<List<Student>>> getStudentsByFawj(
            @Header("Authorization") String token, 
            @Path("fawjId") int fawjId
    );
    
    @POST("students")
    Call<ApiResponse<Student>> createStudent(
            @Header("Authorization") String token, 
            @Body Map<String, Object> student
    );
    
    @PUT("students/{id}")
    Call<ApiResponse<Student>> updateStudent(
            @Header("Authorization") String token, 
            @Path("id") int studentId, 
            @Body Map<String, Object> student
    );
    
    @DELETE("students/{id}")
    Call<ApiResponse<Object>> deleteStudent(
            @Header("Authorization") String token, 
            @Path("id") int studentId
    );
    
    // ═══════════════════════════════════════════════════════════════
    // Halaqat APIs - تم إضافة getAllHalaqat
    // ═══════════════════════════════════════════════════════════════
    
    @GET("halaqat")
    Call<ApiResponse<List<Halaqa>>> getHalaqat(@Header("Authorization") String token);
    
    // ✅ تم إضافة هذه الدالة المفقودة
    @GET("halaqat")
    Call<ApiResponse<List<Halaqa>>> getAllHalaqat(@Header("Authorization") String token);
    
    @POST("halaqat")
    Call<ApiResponse<Halaqa>> createHalaqa(
            @Header("Authorization") String token, 
            @Body Map<String, String> halaqa
    );
    
    @PUT("halaqat/{id}")
    Call<ApiResponse<Halaqa>> updateHalaqa(
            @Header("Authorization") String token, 
            @Path("id") int halaqaId, 
            @Body Map<String, String> halaqa
    );
    
    @DELETE("halaqat/{id}")
    Call<ApiResponse<Object>> deleteHalaqa(
            @Header("Authorization") String token, 
            @Path("id") int halaqaId
    );
    
    // ═══════════════════════════════════════════════════════════════
    // Fawj APIs
    // ═══════════════════════════════════════════════════════════════
    
    @GET("fawj")
    Call<ApiResponse<List<Fawj>>> getAllFawj(@Header("Authorization") String token);
    
    @GET("fawj/halaqa/{halaqaId}")
    Call<ApiResponse<List<Fawj>>> getFawjByHalaqa(
            @Header("Authorization") String token, 
            @Path("halaqaId") int halaqaId
    );
    
    @GET("fawj/teacher/{teacherId}")
    Call<ApiResponse<List<Fawj>>> getFawjByTeacher(
            @Header("Authorization") String token, 
            @Path("teacherId") int teacherId
    );
    
    @POST("fawj")
    Call<ApiResponse<Fawj>> createFawj(
            @Header("Authorization") String token, 
            @Body Map<String, Object> fawj
    );
    
    @PUT("fawj/{id}")
    Call<ApiResponse<Fawj>> updateFawj(
            @Header("Authorization") String token, 
            @Path("id") int fawjId, 
            @Body Map<String, Object> fawj
    );
    
    @DELETE("fawj/{id}")
    Call<ApiResponse<Object>> deleteFawj(
            @Header("Authorization") String token, 
            @Path("id") int fawjId
    );
    
    @POST("fawj/{fawjId}/assign-teacher/{teacherId}")
    Call<ApiResponse<Object>> assignTeacherToFawj(
            @Header("Authorization") String token, 
            @Path("fawjId") int fawjId, 
            @Path("teacherId") int teacherId
    );
    
    @DELETE("fawj/{fawjId}/unassign-teacher/{teacherId}")
    Call<ApiResponse<Object>> unassignTeacherFromFawj(
            @Header("Authorization") String token, 
            @Path("fawjId") int fawjId, 
            @Path("teacherId") int teacherId
    );
    
    @POST("fawj/{fawjId}/assign-student/{studentId}")
    Call<ApiResponse<Object>> assignStudentToFawj(
            @Header("Authorization") String token, 
            @Path("fawjId") int fawjId, 
            @Path("studentId") int studentId
    );
    
    @DELETE("fawj/{fawjId}/unassign-student/{studentId}")
    Call<ApiResponse<Object>> unassignStudentFromFawj(
            @Header("Authorization") String token, 
            @Path("fawjId") int fawjId, 
            @Path("studentId") int studentId
    );
    
    // ═══════════════════════════════════════════════════════════════
    // Attendance APIs
    // ═══════════════════════════════════════════════════════════════
    
    @POST("attendance/mark")
    Call<ApiResponse<Object>> markAttendance(
            @Header("Authorization") String token, 
            @Body List<Map<String, Object>> attendanceList
    );
    
    @GET("attendance/fawj/{fawjId}/date/{date}")
    Call<ApiResponse<List<Attendance>>> getAttendanceByFawjAndDate(
            @Header("Authorization") String token, 
            @Path("fawjId") int fawjId, 
            @Path("date") String date
    );
    
    @GET("attendance/student/{studentId}")
    Call<ApiResponse<List<Attendance>>> getAttendanceByStudent(
            @Header("Authorization") String token, 
            @Path("studentId") int studentId, 
            @Query("startDate") String startDate, 
            @Query("endDate") String endDate
    );
    
    @GET("attendance/report")
    Call<ApiResponse<List<Attendance>>> getAttendanceReport(
            @Header("Authorization") String token, 
            @Query("fawjId") Integer fawjId, 
            @Query("startDate") String startDate, 
            @Query("endDate") String endDate
    );
}
