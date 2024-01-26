package org.springeel.oneclickrecipe.domain.user.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springeel.oneclickrecipe.domain.user.dto.service.UserLoginServiceRequestDto;
import org.springeel.oneclickrecipe.domain.user.dto.service.UserSignUpServiceRequestDto;
import org.springeel.oneclickrecipe.domain.user.entity.User;
import org.springeel.oneclickrecipe.domain.user.entity.UserRole;
import org.springeel.oneclickrecipe.domain.user.exception.AlreadyExistsEmailException;
import org.springeel.oneclickrecipe.domain.user.exception.DuplicateNicknameException;
import org.springeel.oneclickrecipe.domain.user.exception.NotFoundUserException;
import org.springeel.oneclickrecipe.domain.user.exception.NotMatchPasswordException;
import org.springeel.oneclickrecipe.domain.user.exception.UserErrorCode;
import org.springeel.oneclickrecipe.domain.user.mapper.entity.UserEntityMapper;
import org.springeel.oneclickrecipe.domain.user.repository.UserRepository;
import org.springeel.oneclickrecipe.domain.user.service.UserService;
import org.springeel.oneclickrecipe.global.jwt.exception.BadRefreshTokenException;
import org.springeel.oneclickrecipe.global.jwt.exception.JwtErrorCode;
import org.springeel.oneclickrecipe.global.jwt.JwtStatus;
import org.springeel.oneclickrecipe.global.jwt.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void signUp(final UserSignUpServiceRequestDto serviceRequestDto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(serviceRequestDto.email())) {
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }

        // 닉네임이 중복 체크
        if (userRepository.existsByNickname(serviceRequestDto.nickname())) {
            throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        // 비밀번호가 같은지 확인
        if (!Objects.equals(serviceRequestDto.password(), serviceRequestDto.confirmPassword())) {
            throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
        }

        User user = userEntityMapper.toUser(serviceRequestDto, UserRole.USER);

        userRepository.save(user);
    }

    @Override
    public void login(
        final UserLoginServiceRequestDto serviceRequestDto,
        final HttpServletResponse httpServletResponse
    ) {
        User user = userRepository.findByEmail(serviceRequestDto.email())
            .orElseThrow(() -> new NotFoundUserException(UserErrorCode.BAD_LOGIN));

        if (!passwordEncoder.matches(serviceRequestDto.password(), user.getPassword())) {
            throw new NotMatchPasswordException(UserErrorCode.BAD_LOGIN);
        }

        jwtUtil.addJwtToHeader(user, httpServletResponse);
    }

    @Override
    public void refreshAccessToken(final String refreshToken, final User user,
        final HttpServletResponse httpServletResponse) {
        String token = refreshToken.substring(7);
        JwtStatus jwtStatus = jwtUtil.validateToken(token);
        if(jwtStatus != JwtStatus.ACCESS){
            throw new BadRefreshTokenException(JwtErrorCode.INVALID_TOKEN);
        }

        jwtUtil.addAccessTokenToHeader(user, httpServletResponse);
    }
}
