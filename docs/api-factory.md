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
