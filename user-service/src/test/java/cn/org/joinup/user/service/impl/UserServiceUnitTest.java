package cn.org.joinup.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.user.config.JwtProperties;
import cn.org.joinup.user.config.UserDefaultAvatarProperties;
import cn.org.joinup.user.config.UserRegisterProperties;
import cn.org.joinup.user.domain.dto.LoginFormDTO;
import cn.org.joinup.user.domain.dto.RegisterFormDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.UserLoginVO;
import cn.org.joinup.user.enums.UserType;
import cn.org.joinup.user.mapper.UserMapper;
import cn.org.joinup.user.util.JwtTool;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTool jwtTool;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private WxMaService wxMaService;
    @Mock
    private SensitiveWordBs sensitiveWordBs;
    @Mock
    private UserRegisterProperties userRegisterProperties;
    @Mock
    private UserDefaultAvatarProperties userDefaultAvatarProperties;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private RedissonClient redissonClient;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // Construct the service with mocked dependencies
        userService = spy(new UserServiceImpl(
                passwordEncoder,
                jwtTool,
                jwtProperties,
                stringRedisTemplate,
                wxMaService,
                redissonClient,
                sensitiveWordBs,
                userRegisterProperties,
                userDefaultAvatarProperties
        ));
        
        // Inject the baseMapper required by MyBatis-Plus ServiceImpl
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        String username = "testUser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String token = "jwt-token";

        LoginFormDTO loginDTO = new LoginFormDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setUserType(UserType.INTERNAL);
        user.setRole("USER");
        user.setAppKey("appKey");

        // Mock lambdaQuery chain
        LambdaQueryChainWrapper<User> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(userService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(user);

        // Mock password check
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Mock JWT creation
        when(jwtProperties.getTokenTTL()).thenReturn(Duration.ofMinutes(3600L));
        when(jwtTool.createToken(any(), any(), any(), any(), any(Duration.class))).thenReturn(token);

        // Act
        UserLoginVO result = userService.login(loginDTO);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(token, result.getToken());
        
        verify(userService).lambdaQuery();
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtTool).createToken(eq(1L), eq("USER"), eq("appKey"), eq(UserType.INTERNAL), eq(Duration.ofMinutes(3600L)));
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginFormDTO loginDTO = new LoginFormDTO();
        loginDTO.setUsername("nonExistentUser");
        loginDTO.setPassword("password");

        // Mock lambdaQuery chain to return null
        LambdaQueryChainWrapper<User> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(userService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(null);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.login(loginDTO));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        // Arrange
        String username = "testUser";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";

        LoginFormDTO loginDTO = new LoginFormDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setUserType(UserType.INTERNAL);

        // Mock lambdaQuery chain
        LambdaQueryChainWrapper<User> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(userService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(user);

        // Mock password check failure
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.login(loginDTO));
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void testRegister_Success() {
        // Arrange
        String username = "newUser";
        String password = "password";
        String email = "test@example.com";
        String code = "123456";
        String token = "jwt-token";

        RegisterFormDTO registerDTO = new RegisterFormDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword(password);
        registerDTO.setEmail(email);
        registerDTO.setVerifyCode(code);

        // Mock properties
        when(userRegisterProperties.isEmailEnabled()).thenReturn(true);

        // Mock lambdaQuery chain (User not found)
        LambdaQueryChainWrapper<User> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(userService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(null);

        // Mock Redis verify code
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(code);

        // Mock password encoding
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Mock save (MyBatis-Plus baseMapper)
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // Mock JWT
        when(jwtProperties.getTokenTTL()).thenReturn(Duration.ofMinutes(3600L));
        when(jwtTool.createToken(any(), any(), any(), any(), any(Duration.class))).thenReturn(token);

        // Act
        UserLoginVO result = userService.register(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(token, result.getToken());

        verify(userService).lambdaQuery();
        verify(userMapper).insert(any(User.class));
        verify(stringRedisTemplate).delete(anyString());
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange
        String username = "existingUser";
        RegisterFormDTO registerDTO = new RegisterFormDTO();
        registerDTO.setUsername(username);
        registerDTO.setEmail("test@example.com");

        // Mock properties
        when(userRegisterProperties.isEmailEnabled()).thenReturn(true);

        // Mock lambdaQuery chain (User found)
        LambdaQueryChainWrapper<User> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(userService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(new User());

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.register(registerDTO));
        assertEquals("用户名已存在", exception.getMessage());
    }
}
