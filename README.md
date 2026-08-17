
# hazalcast
Implementation of hazalcast on java 17 and spring boot 4

# Spring Boot + Hazelcast Embedded Distributed Caching

This repository is a proof-of-concept and reference project demonstrating how to implement **Hazelcast Embedded Distributed Caching** in a Spring Boot application. 

It is designed for seamless operation in both **local development** and **Rancher / Kubernetes clustered environments**, utilizing **Multicast** for local dev and **TCP/IP Discovery with a Headless Service** for Kubernetes pods.

---

## 🚀 Key Features

* **Embedded Mode**: Hazelcast runs within the Spring Boot application JVM, eliminating the need to maintain a separate Hazelcast server cluster.
* **Dual Discovery Modes**:
  * **Multicast Discovery (Local Dev)**: Automatically enabled for local development so instances on the same machine/network form a cluster without configuration.
  * **TCP/IP Headless Service Discovery (Rancher / Kubernetes)**: Uses Kubernetes Headless Service DNS resolution to discover peer pods. **No Kubernetes API plugins or RBAC permissions required.**
* **Spring Cache Abstraction**: Leverages standard Spring annotations (`@Cacheable`, `@CachePut`, `@CacheEvict`) backed by Hazelcast's distributed `IMap`.
* **Container Ready**: Includes custom `Dockerfile` configured to launch the pre-packaged application with external configuration support.

---

## 📁 Project Structure

```text
src/main/java/com/f1soft/hazelcast/
├── HazelcastApplication.java               # Main entry point with @EnableCaching
├── config/
│   └── HazelcastConfiguration.java         # Embedded Hazelcast & CacheManager configuration
├── controller/
│   └── BalanceCertificateRequestController.java # REST API endpoints for testing
├── model/
│   └── BalanceCertificateRequest.java      # Entity model (cached object)
├── repository/
│   └── BalanceCertificateRequestRepository.java # JPA Repository
└── service/
    └── BalanceCertificateRequestService.java    # Business logic with cache annotations
```

---

## 🛠️ Technology Stack

* **Java**: 17
* **Framework**: Spring Boot 3.x / 4.x
* **Cache Provider**: Hazelcast IMDG (`hazelcast-spring`)
* **Persistence**: Spring Data JPA / Hibernate
* **Database**: MySQL

---

## 💻 Local Setup & Testing

### Prerequisites
* JDK 17
* Maven 3.8+
* Running MySQL instance (configured in `application.yaml`)

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/spring-boot-hazelcast-embedded.git
   cd spring-boot-hazelcast-embedded
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

*(By default `hazelcast.local.dev=true` is active, enabling multicast discovery for local testing).*

---

## 📡 REST API Reference

The project includes endpoints to test cache behavior on `BalanceCertificateRequest`:

| HTTP Method | Endpoint | Description | Cache Behavior |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/balance-certificates/{id}` | Fetch certificate request by ID | `@Cacheable` (DB hit on first call, cache hit on subsequent calls) |
| `GET` | `/api/balance-certificates` | Fetch all records | Direct DB query |
| `POST` | `/api/balance-certificates` | Create/update certificate request | `@CachePut` (Updates database & cache) |
| `DELETE` | `/api/balance-certificates/{id}` | Delete certificate request | `@CacheEvict` (Deletes from DB & evicts from cache) |

---

## ☸️ Rancher / Kubernetes Deployment (TCP/IP + Headless Service)

In Kubernetes/Rancher, pods have dynamic IP addresses and multicast is typically blocked. We use **TCP/IP Discovery over a Kubernetes Headless Service** to let pods discover each other.

### 1. Create a Headless Service (`hazelcast-headless.yaml`)

Create a Headless Service (`clusterIP: None`) that targets all pods in your application workload.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: hazelcast-headless
  namespace: default
spec:
  clusterIP: None # <-- Crucial: Headless service
  selector:
    app: bank-connect # <-- Must match your deployment's pod label
  ports:
    - name: hazelcast
      port: 5701
      targetPort: 5701
```

### 2. Application Deployment (`deployment.yaml`)

Deploy your application pods, setting `HAZELCAST_LOCAL_DEV=false` and pointing `HAZELCAST_HEADLESS_SERVICE_DNS` to your headless service DNS.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bank-connect
  namespace: default
spec:
  replicas: 3
  selector:
    matchLabels:
      app: bank-connect
  template:
    metadata:
      labels:
        app: bank-connect
    spec:
      containers:
        - name: bank-connect
          image: your-registry/bank-connect:latest
          ports:
            - containerPort: 8080 # HTTP REST API
            - containerPort: 5701 # Hazelcast Cluster Port
          env:
            - name: HAZELCAST_LOCAL_DEV
              value: "false"
            - name: HAZELCAST_HEADLESS_SERVICE_DNS
              value: "hazelcast-headless.default.svc.cluster.local"
```

### 3. How Cluster Formation & Cache Sharing Works Across Deployments

#### Multi-Deployment Single-Headless-Service Architecture
If you have **multiple distinct deployments** (e.g. 10 microservice deployments with 2 pods each) and want all 20 pods to join **a single shared Hazelcast cluster**, you only need **1 Headless Service**:

1. **Shared Pod Label**: Add a common label (e.g., `hazelcast-cluster: shared`) to the pod template of all your deployments.
2. **Single Headless Service**: Configure the Headless Service's `selector` to match that shared label (`hazelcast-cluster: shared`).
3. **DNS Resolution**: When any pod queries `hazelcast-headless.default.svc.cluster.local`, Kubernetes DNS resolves to the IP addresses of **all pods across every deployment** carrying that label.
4. **Mesh Clustering & Cache Sync**:
   * Each embedded Hazelcast instance receives the complete IP list and connects over port `5701`.
   * All 20 pods form a single unified Hazelcast cluster (`Members [20]`).
   * **Cache writes on any pod in Deployment 1 are instantly shared and accessible by any pod in Deployment 10.**

---

## 🐳 Docker Support

A `Dockerfile` is provided in the project root:

```bash
# 1. Build the jar locally
./mvnw clean package -DskipTests

# 2. Build the Docker image
docker build -t hazelcast-app:latest .

# 3. Run the container
docker run -p 8080:8080 -p 5701:5701 hazelcast-app:latest
```

---

## 📝 License

This project is licensed under the MIT License - feel free to use and adapt for your research and production workloads.
