# JSON Transform Library

A utility library for creating Retrofit service instances with automatic JSON transformation capabilities using Jolt specifications.

---

## Installation

Add the following dependency to your `build.gradle`:

```gradle
implementation 'com.github.Six-Sprints:json-transform:1.0.0'
```

And ensure you have the required repositories:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

---

# ApiFactory Documentation

**Fully Qualified Name:** `com.sixsprints.json.util.ApiFactory`

**Import Statement:**

```java
import com.sixsprints.json.util.ApiFactory;
```

The `ApiFactory` class is a utility class that provides methods for creating Retrofit service instances and making API calls with automatic JSON transformation capabilities.

## Public Methods

### 1. create

#### Signature

```java
public static <T> T create(Class<T> clazz, String baseUrl, ObjectMapper mapper)
```

**Purpose:** Creates a Retrofit service instance for the given interface class using a custom ObjectMapper.

**Parameters:**

- `clazz` - The Retrofit service interface class to instantiate
- `baseUrl` - The base URL for the API endpoints
- `mapper` - A custom Jackson ObjectMapper for JSON serialization/deserialization

**Returns:** An instance of the specified Retrofit service interface

---

#### Signature

```java
public static <T> T create(Class<T> clazz, String baseUrl)
```

**Purpose:** Creates a Retrofit service instance for the given interface class using the default ObjectMapper.

**Parameters:**

- `clazz` - The Retrofit service interface class to instantiate
- `baseUrl` - The base URL for the API endpoints

**Returns:** An instance of the specified Retrofit service interface

---

### 2. retrofit

#### Signature

```java
public static Builder retrofit(String baseUrl, ObjectMapper mapper)
```

**Purpose:** Creates a Retrofit Builder with custom configuration and ObjectMapper. This allows for further customization before building the Retrofit instance.

**Parameters:**

- `baseUrl` - The base URL for the API endpoints
- `mapper` - A custom Jackson ObjectMapper for JSON serialization/deserialization

**Returns:** A configured `Retrofit.Builder` instance

**Note:** The builder is pre-configured with:

- OkHttpClient with 30s connect timeout, 60s read timeout, and 30s write timeout
- ScalarsConverterFactory for handling plain strings
- JacksonConverterFactory for JSON conversion

---

#### Signature

```java
public static Builder retrofit(String baseUrl)
```

**Purpose:** Creates a Retrofit Builder with default ObjectMapper configuration.

**Parameters:**

- `baseUrl` - The base URL for the API endpoints

**Returns:** A configured `Retrofit.Builder` instance

---

### 3. makeCallAndTransform

#### Signature

```java
public static <T> T makeCallAndTransform(ApiCall apiCall, Class<T> clazz)
    throws IOException, ApiException
```

**Purpose:** Executes an API call and transforms the response to the specified class type. This method handles both primitive wrapper types and complex object types.

**Parameters:**

- `apiCall` - An `ApiCall` object containing the Retrofit call, mapping configuration, and optional custom ObjectMapper
- `clazz` - The target class to convert the transformed response into

**Returns:** The transformed response as an instance of the specified class

**Throws:**

- `IOException` - If an I/O error occurs during the API call
- `ApiException` - If the API response indicates failure

---

#### Signature

```java
public static <T> T makeCallAndTransform(ApiCall apiCall, TypeReference<T> type)
    throws IOException, ApiException
```

**Purpose:** Executes an API call and transforms the response to the specified type using Jackson's TypeReference. This is useful for generic collections (e.g., `List<User>`, `Map<String, Object>`).

**Parameters:**

- `apiCall` - An `ApiCall` object containing the Retrofit call, mapping configuration, and optional custom ObjectMapper
- `type` - A Jackson `TypeReference` representing the target type

**Returns:** The transformed response as an instance of the specified type

**Throws:**

- `IOException` - If an I/O error occurs during the API call
- `ApiException` - If the API response indicates failure

---

#### Signature

```java
public static <T> T makeCallAndTransform(ApiCall apiCall, JavaType type)
    throws IOException, ApiException
```

**Purpose:** Executes an API call and transforms the response to the specified JavaType. This provides maximum flexibility for complex type construction.

**Parameters:**

- `apiCall` - An `ApiCall` object containing the Retrofit call, mapping configuration, and optional custom ObjectMapper
- `type` - A Jackson `JavaType` representing the target type

**Returns:** The transformed response as an instance of the specified type

**Throws:**

- `IOException` - If an I/O error occurs during the API call
- `ApiException` - If the API response indicates failure

---

## Default ObjectMapper Configuration

The class maintains a static default ObjectMapper with the following configurations:

- `FAIL_ON_UNKNOWN_PROPERTIES` is disabled (allows ignoring unknown JSON fields)
- `ACCEPT_EMPTY_STRING_AS_NULL_OBJECT` is enabled (treats empty strings as null)
- `ACCEPT_SINGLE_VALUE_AS_ARRAY` is enabled (accepts single values where arrays are expected)
- TimeZone is set to the system default

This default mapper is used when no custom ObjectMapper is provided.

---

# Retrofit Spring Integration Guide

This guide demonstrates how to create a Retrofit service interface and configure it as a Spring bean using `ApiFactory`.

## Step 1: Define Your Retrofit Service Interface

Create a Retrofit service interface with your API endpoints:

```java
package com.example.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    @GET("users/{id}")
    Call<String> getUserById(@Path("id") String userId);

    @GET("users")
    Call<String> getAllUsers(@Query("page") int page, @Query("size") int size);

    @POST("users")
    Call<String> createUser(@Body Object userRequest);

    @GET("users/search")
    Call<String> searchUsers(@Query("name") String name);
}
```

**Note:** The return type should be `Call<String>` as ApiFactory's `makeCallAndTransform` methods expect string responses that will be transformed.

---

## Step 2: Create a Spring Configuration Class

Create a configuration class to define your Retrofit service as a Spring bean:

```java
package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.api.UserApiService;
import com.sixsprints.json.util.ApiFactory;

@Configuration
public class RetrofitConfig {

    @Value("${api.user.baseUrl}")
    private String userApiBaseUrl;

    @Bean
    public UserApiService userApiService() {
        return ApiFactory.create(UserApiService.class, userApiBaseUrl);
    }
}
```

**Configuration Notes:**

- The `@Value` annotation injects the base URL from application properties
- The `@Bean` annotation makes the service available for dependency injection
- `ApiFactory.create()` uses the default ObjectMapper configuration

---

## Step 2 (Alternative): Using Custom ObjectMapper

If you need custom JSON serialization/deserialization behavior:

```java
package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.api.UserApiService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sixsprints.json.util.ApiFactory;

@Configuration
public class RetrofitConfig {

    @Value("${api.user.baseUrl}")
    private String userApiBaseUrl;

    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }

    @Bean
    public UserApiService userApiService(ObjectMapper customObjectMapper) {
        return ApiFactory.create(UserApiService.class, userApiBaseUrl, customObjectMapper);
    }
}
```

---

## Step 3: Add Configuration to application.properties

Add your API base URL to `application.properties`:

```properties
# User API Configuration
api.user.baseUrl=https://api.example.com/v1/
```

Or in `application.yml`:

```yaml
api:
  user:
    baseUrl: https://api.example.com/v1/
```

---

## Step 4: Use the Service in Your Components

Inject and use the Retrofit service in your Spring components.

### Simple Usage (Without Mapping)

In most cases, the API response can be directly mapped to simple DTOs without requiring a transformation spec:

```java
package com.example.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.api.UserApiService;
import com.example.dto.User;
import com.sixsprints.json.dto.ApiCall;
import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.json.exception.ApiException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserApiService userApiService;

    public User getUserById(String userId) throws IOException, ApiException {
        // No mapping needed - direct transformation to DTO
        ApiCall apiCall = ApiCall.builder()
            .call(userApiService.getUserById(userId))
            .build();

        return ApiFactory.makeCallAndTransform(apiCall, User.class);
    }

    public List<User> getAllUsers(int page, int size) throws IOException, ApiException {
        ApiCall apiCall = ApiCall.builder()
            .call(userApiService.getAllUsers(page, size))
            .build();

        return ApiFactory.makeCallAndTransform(
            apiCall,
            new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {}
        );
    }
}
```

### Advanced Usage (With Mapping)

When you need to transform complex API responses, you can use a Jolt mapping specification. The `mapping` parameter is **optional** and only needed when the API response structure doesn't match your DTOs.

```java
package com.example.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.api.UserApiService;
import com.example.dto.User;
import com.sixsprints.json.dto.ApiCall;
import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.json.exception.ApiException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserApiService userApiService;

    public User getUserById(String userId) throws IOException, ApiException {
        // With mapping - for complex transformations
        ApiCall apiCall = ApiCall.builder()
            .call(userApiService.getUserById(userId))
            .mapping(createUserMapping())
            .build();

        return ApiFactory.makeCallAndTransform(apiCall, User.class);
    }

    private Mapping createUserMapping() {
        // Create your Jolt mapping configuration for transforming the API response
        return Mapping.builder()
            .spec("user-mapping-spec.json")
            .build();
    }
}
```

---

## Step 5: Understanding Jolt Mapping Specifications

The mapping specification uses **Jolt** (JSON to JSON transformation library). Learn more at [https://github.com/bazaarvoice/jolt](https://github.com/bazaarvoice/jolt).

### Example Jolt Mapping

Suppose your API returns this response:

```json
{
  "data": {
    "user_id": "123",
    "user_name": "John Doe",
    "contact": {
      "email_address": "john@example.com",
      "phone_number": "+1234567890"
    },
    "metadata": {
      "created_at": "2023-01-15",
      "status": "active"
    }
  }
}
```

And you want to transform it to match your DTO:

```json
{
  "id": "123",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "status": "active"
}
```

Create a Jolt spec file (`user-mapping-spec.json`):

```json
[
  {
    "operation": "shift",
    "spec": {
      "data": {
        "user_id": "id",
        "user_name": "name",
        "contact": {
          "email_address": "email",
          "phone_number": "phone"
        },
        "metadata": {
          "status": "status"
        }
      }
    }
  }
]
```

### Common Jolt Operations

1. **shift** - Move data from one location to another
2. **default** - Add default values
3. **remove** - Remove unwanted fields
4. **sort** - Sort keys alphabetically

### Example: Handling Arrays

API Response:

```json
{
  "users": [
    {
      "user_id": "1",
      "full_name": "Alice"
    },
    {
      "user_id": "2",
      "full_name": "Bob"
    }
  ]
}
```

Jolt Spec to transform to simpler structure:

```json
[
  {
    "operation": "shift",
    "spec": {
      "users": {
        "*": {
          "user_id": "[&1].id",
          "full_name": "[&1].name"
        }
      }
    }
  }
]
```

Output:

```json
[
  {
    "id": "1",
    "name": "Alice"
  },
  {
    "id": "2",
    "name": "Bob"
  }
]
```

---

## Summary

1. **Define** a Retrofit service interface with `Call<String>` return types
2. **Configure** the service as a Spring bean using `ApiFactory.create()`
3. **Inject** base URLs from application properties
4. **Use** the service in your components with `ApiFactory.makeCallAndTransform()`
5. **Optionally** use Jolt mapping specifications for complex API response transformations

This approach provides:

- Clean separation of concerns
- Easy dependency injection
- Centralized API configuration
- Direct DTO mapping for simple cases
- Powerful Jolt transformations for complex response structures
- Flexibility for customization
