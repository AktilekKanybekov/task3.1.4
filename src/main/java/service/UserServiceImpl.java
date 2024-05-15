package service;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService{
    private final RestTemplate restTemplate;

    private String sessionId;

    private String URL = "http://94.198.50.185:7081/api/users/";

    @Autowired
    public UserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeadersWithSessionId(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie",sessionId);
        return headers;
    }

    private ResponseEntity<String>sendRequestWithMethod(String url, HttpMethod method, HttpEntity<?> request){
        HttpHeaders headers = createHeadersWithSessionId();
        HttpEntity<?> requestWithHeaders = new HttpEntity<>(request.getBody(), headers);
        return restTemplate.exchange(URL, method, requestWithHeaders, String.class);
    }

    @Override
    public ResponseEntity<User[]> getAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(URL, User[].class);
        User[] users = response.getBody();

        Arrays.stream(users).forEach(user -> System.out.println(user.toString()));
        System.out.println();

        sessionId = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        return ResponseEntity.ok(users);
    }

    @Override
    public String createUser(User user) {
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity<String> response = sendRequestWithMethod(URL, HttpMethod.POST, request);
        return response.getBody();
    }

    @Override
    public String updateUser(User user) {
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity<String> response = sendRequestWithMethod(URL, HttpMethod.PUT, request);
        return response.getBody();
    }

    @Override
    public String deleteUser(Long id) {
        String urlDelete = URL + id;
        HttpEntity<Void> request = new HttpEntity<>(createHeadersWithSessionId());
        ResponseEntity<String> response = restTemplate.exchange(urlDelete, HttpMethod.DELETE, request, String.class);
        return response.getBody();
    }
}
