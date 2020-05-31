package com.greenfoxacademy.jwtretrofittesenvmocking.controller;

import com.greenfoxacademy.jwtretrofittesenvmocking.model.dto.UserDTO;
import com.greenfoxacademy.jwtretrofittesenvmocking.service.UserService;
import com.greenfoxacademy.jwtretrofittesenvmocking.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(PowerMockRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@PrepareForTest(System.class)
public class ApiControllerTest {

  @Autowired
  private MockMvc mockMvc;
  private UserService userService;
  private UserDetailsService userDetailsService;
  private JwtUtil jwtUtil;

  @Autowired
  public ApiControllerTest(UserService userService,
                           UserDetailsService userDetailsService,
                           JwtUtil jwtUtil) {
    this.userService = userService;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
    this.userService.saveUser(new UserDTO("default", "password"));
  }

  @Test
  public void registerNewUser_WithValidUserDto_ReturnsValidStatusAndObject() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"test\",\"password\":\"password\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username", is("test")));
  }

  @Test
  public void registerNewUser_WithEmptyPassword_ReturnsValidStatusAndObject() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"test\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error", is("Username and password fields are " +
            "incorrect or missing.")));
  }

  @Test
  public void registerNewUser_WithEmptyUsername_ReturnsValidStatusAndObject() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"\",\"password\":\"password\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error", is("Username and password fields are " +
            "incorrect or missing.")));
  }

  @Test
  public void registerNewUser_WithMissingPassword_ReturnsValidStatusAndObject() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"test\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error", is("Username and password fields are " +
            "incorrect or missing.")));
  }

  @Test
  public void registerNewUser_WithMissingUsername_ReturnsValidStatusAndObject() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"password\":\"password\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error", is("Username and password fields are " +
            "incorrect or missing.")));
  }

  @Test
  public void getJwtTokenAndAuthenticateUser_WithValidAuthenticationRequestDTO_ReturnsValidStatusAndObject() throws Exception {
    /*System.setProperty("test-prop", "test-value");
    PowerMockito.mockStatic(System.class);
    PowerMockito.when(System.getenv("SECRET_KEY")).thenReturn("testKey");*/

    mockMvc.perform(post("/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"default\",\"password\":\"password\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.jwt").exists());
  }

  @Test
  public void getJwtTokenAndAuthenticateUser_WithValidInvalidUsername_ReturnsValidStatus() throws Exception {
    mockMvc.perform(post("/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"defaultasd\",\"password\":\"password\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getJwtTokenAndAuthenticateUser_WithValidInvalidPassword_ReturnsValidStatus() throws Exception {
    mockMvc.perform(post("/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"default\",\"password\":\"passwordasd\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getTestString_WithoutAuthentication_ReturnsValidStatus() throws Exception {
    mockMvc.perform(get("/test"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void getTestString_WithAuthentication_ReturnsValidStatusAndContent() throws Exception {
    UserDetails userDetails = userDetailsService.loadUserByUsername("default");
    String jwt = jwtUtil.generateToken(userDetails);

    mockMvc.perform(get("/test")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk())
        .andExpect(content().string("Test"));
  }

  @Test
  public void getPopularMovies_WithoutAuthentication_ReturnsValidStatus() throws Exception {
    mockMvc.perform(get("/popular-movies"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void getPopularMovies_WithAuthentication_ReturnsValidStatusAndObject() throws Exception {
    UserDetails userDetails = userDetailsService.loadUserByUsername("default");
    String jwt = jwtUtil.generateToken(userDetails);
    mockMvc.perform(get("/popular-movies")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk());
  }
}