package com.yaser.news.service;

import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.dataEntity.Labels;
import com.yaser.news.dataEntity.RecUser;
import com.yaser.news.repository.LabelsRepository;
import com.yaser.news.repository.RecUserRepository;
import com.yaser.news.service.dataWrap.UserInfoWrap;
import com.yaser.news.service.dataWrap.UserLoginWrap;
import com.yaser.news.utils.JWTUtils;
import com.yaser.news.utils.ServiceContextHolder;
import com.yaser.news.utils.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class UserService {
    private final RecUserRepository recUserRepository;
    private final LabelsRepository labelsRepository;

    public UserService(RecUserRepository recUserRepository, LabelsRepository labelsRepository) {
        this.recUserRepository = recUserRepository;
        this.labelsRepository = labelsRepository;
    }

    public UserLoginWrap login(String email, String password) {
        RecUser recUser = this.recUserRepository.findByEmail(email);
        if (recUser == null) throw new APIException(ResultCode.USER_NOT_EXIST_ERROR);
        if (!recUser.getPassword().equals(password)) throw new APIException(ResultCode.USER_PASSWORD_ERROR);

        UserLoginWrap userLoginWrap = new UserLoginWrap();
        BeanUtils.copyProperties(recUser, userLoginWrap);
        userLoginWrap.setToken(JWTUtils.generateToken(recUser));
        return userLoginWrap;
    }

    public RecUser loadUserByUserId(long userId) {
        RecUser recUser = this.recUserRepository.findByUid(userId);
        if (recUser == null) throw new APIException(ResultCode.USER_NOT_EXIST_ERROR);
        return recUser;
    }

    public UserInfoWrap getSelfInfo() {
        RecUser recUser = ServiceContextHolder.getContext().getRecUser();//直接从线程中读取信息
        UserInfoWrap userInfoWrap = new UserInfoWrap();
        BeanUtils.copyProperties(recUser, userInfoWrap);
        return userInfoWrap;
    }

    public UserInfoWrap getUserInfoById(long userId) {
        RecUser recUser = this.recUserRepository.findByUid(userId);
        if (recUser == null) throw new APIException(ResultCode.USER_NOT_EXIST_ERROR);
        UserInfoWrap userInfoWrap = new UserInfoWrap();
        BeanUtils.copyProperties(recUser, userInfoWrap);
        return userInfoWrap;
    }



    public UserLoginWrap register(String email, String password, String nickname, int gender) {
        if (this.recUserRepository.existsByEmail(email)) throw new APIException(ResultCode.USER_EXIST);
        RecUser newRecUser = new RecUser();
        newRecUser.setEmail(email);
        newRecUser.setGender(gender);
        newRecUser.setNickname(nickname);
        newRecUser.setPassword(password);
        newRecUser.setUid(SnowflakeIdGenerator.getId());
        RecUser savedRecUser = this.recUserRepository.save(newRecUser);
        log.info(savedRecUser.toString());
        UserLoginWrap userLoginWrap = new UserLoginWrap();
        BeanUtils.copyProperties(savedRecUser, userLoginWrap);
        userLoginWrap.setToken(JWTUtils.generateToken(savedRecUser));
        return userLoginWrap;
    }
}
