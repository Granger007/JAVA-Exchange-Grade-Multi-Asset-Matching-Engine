# Production-Grade Multi-Asset Trading Engine

A comprehensive production-grade trading engine architecture demonstrating clean software engineering principles with strict separation between production code and educational bad design examples.

##  Project Overview

This project implements a sophisticated multi-asset trading engine with two distinct layers:

### Production Code (Safe for Production Use)
- **Clean Architecture**: 4-layer architecture following SOLID and GRASP principles
- **Domain-Driven Design**: Rich domain models with proper encapsulation
- **Design Patterns**: Strategy, Observer, Factory, Repository patterns
- **REST API**: Spring Boot-based endpoints with proper dependency injection
- **Multi-Asset Support**: Extensible framework for equities, crypto, futures, options

### Educational Bad Design (Learning Only)
- **Anti-Patterns**: God objects, tight coupling, encapsulation violations
- **Isolated Package**: Completely separate from production code
- **Safe Execution**: Manual demo runner for educational purposes
- **Clear Documentation**: Each violation explained with solutions

##  Package Structure

### Production Code (Safe to Use)
```java
com.tradingengine/
|
controller/              # REST Controllers (MVC)
    - TradingController.java
|
service/                 # Business Logic
    - OrderService.java
    - TradeService.java
    - MarketDataService.java
|
domain/                  # Core Models
    model/
        - Order.java
        - Trade.java
        - OrderBook.java
        - PriceLevel.java
        - OrderSide.java
        - OrderStatus.java
|
strategy/                # Design Patterns
    - MatchingStrategy.java
    - FIFOMatchingStrategy.java
    - ProRataMatchingStrategy.java
|
factory/                 # Engine Creation
    - MatchingEngineFactory.java
|
observer/                # Market Data
    - MarketDataPublisher.java
    - MarketDataListener.java
    - MarketDataEvent.java
|
repository/              # Data Access
    - OrderRepository.java
    - TradeRepository.java
    - impl/
        - InMemoryOrderRepository.java
        - InMemoryTradeRepository.java
|
dto/                     # Data Transfer Objects
    - OrderRequest.java
    - OrderResponse.java
```

### Bad Design Code (Educational Only)
```java
com.tradingengine.bad_design/
|
god_objects/
    - GodMatchingEngine.java       # SRP violation
|
tight_coupling/
    - TightlyCoupledController.java # DIP violation
|
encapsulation_violation/
    - UnsafeOrderBook.java        # Information hiding violation
|
no_strategy_pattern/
    - HardcodedMatchingEngine.java # OCP violation
|
fat_controller/
    - FatController.java          # GRASP Controller violation
|
demo_runner/
    - BadDesignDemoRunner.java   # Safe execution of bad examples
```

##  Features

### Production Features
-  **Clean Architecture**: 4-layer separation (Controller, Service, Domain, Infrastructure)
-  **SOLID Principles**: SRP, OCP, LSP, ISP, DIP fully applied
-  **GRASP Patterns**: Information Expert, Creator, Controller, Low Coupling, High Cohesion
-  **Design Patterns**: Strategy, Observer, Factory, Repository, Builder
-  **Thread Safety**: Concurrent access support in order book and market data
-  **Type Safety**: Immutable domain objects and proper validation
-  **Extensibility**: Easy to add new assets, strategies, and features

### Educational Features
-  **Bad Design Examples**: Isolated anti-patterns with clear explanations
-  **Safe Demo Runner**: Manual execution without affecting production
-  **Comparative Learning**: Side-by-side good vs bad implementations
-  **Principle Violations**: Each violation clearly documented and explained

### Core Trading Engine
-  **Order Matching**: FIFO and Pro-Rata matching algorithms with Strategy pattern
-  **Multi-Asset Support**: Extensible framework for equities, crypto, futures, options
-  **Real-time Market Data**: Observer pattern for live price updates
-  **Order Management**: Complete order lifecycle with proper state transitions
-  **Stop Loss Orders**: Automatic order triggering based on price thresholds
-  **Market Orders**: Immediate execution at best available market prices
-  **Portfolio Management**: Real-time portfolio tracking with balance and position updates

### API & Monitoring
-  **REST Endpoints**: Clean Spring Boot controllers with dependency injection
-  **DTOs**: Proper request/response objects with validation
-  **Error Handling**: Comprehensive exception management
-  **Transaction Management**: ACID compliance for order processing
-  **WebSocket Support**: Real-time updates via STOMP over WebSocket
-  **Web Dashboard**: Aurora Simulation Dashboard with live trading visualization
-  **Simulation API**: Dedicated endpoints for simulation control and monitoring

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   REST API      │    │  Simulation     │    │   Market Data   │
│  (Controller)   │    │    Engine       │    │    Feed         │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          ▼                      ▼                      ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Order Book      │    │ Trading Agents  │    │ Performance     │
│    Manager      │    │                 │    │    Tracker      │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          ▼                      ▼                      ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Matching Engine │    │ Order Gateway   │    │ Trade History   │
│                 │    │                 │    │    Logger       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🎯 Design Principles Analysis

This project deliberately demonstrates both **good** and **bad** design principles to provide educational value:

### ✅ Good Design Principles Demonstrated

#### 1. **Single Responsibility Principle (SRP)**
- `OrderBook`: Manages only order storage and retrieval
- `MatchingEngine`: Handles only order matching logic
- `TradeHistoryLogger`: Solely responsible for trade logging
- `AgentPerformanceTracker`: Exclusively tracks agent metrics

#### 2. **Open/Closed Principle (OCP)**
- `MatchingStrategy` interface allows adding new matching algorithms without modifying existing code
- `TradingAgent` interface enables adding new agent types
- `MarketDataListener` allows adding new market data consumers

#### 3. **Dependency Inversion Principle (DIP)**
- Constructor injection in `OrderController` enables loose coupling
- Interface-based design for trading strategies and agents
- Spring Boot's dependency injection framework

#### 4. **Strategy Pattern**
- `FIFOMatching` and `ProRataMatching` implementations
- Pluggable matching algorithms for different market types

#### 5. **Observer Pattern**
- `MarketDataListener` for real-time updates
- Multiple observers (feed, logger, performance tracker) for same events

#### 6. **Builder Pattern**
- `OrderBookSnapshotBuilder` for constructing complex DTOs
- Clean API for order book snapshot creation

### ❌ Bad Design Principles Demonstrated (with Explanations)

#### 1. **Encapsulation Violation in OrderBook**
```java
// BAD: Direct exposure of internal data structures
public TreeMap<Double, List<Order>> getBids() {
    return bids; // Allows external mutation
}

// GOOD: Unmodifiable view
public NavigableMap<Double, List<Order>> getBidsView() {
    return Collections.unmodifiableNavigableMap(bids);
}
```

#### 2. **Tight Coupling in Controller (Fixed)**
```java
// BAD: Direct instantiation
public OrderController() {
    this.orderBookManager = new OrderBookManager();
}

// GOOD: Dependency injection
public OrderController(OrderBookManager orderBookManager) {
    this.orderBookManager = orderBookManager;
}
```

#### 3. **Information Hiding Violation in API (Fixed)**
```java
// BAD: Exposing internal domain models
@GetMapping("/orderbook/{symbol}")
public Map<String, Object> getOrderBook(@PathVariable String symbol) {
    return Map.of("bids", book.getBids(), "asks", book.getAsks());
}

// GOOD: Clean DTO with Builder pattern
@GetMapping("/orderbook/{symbol}")
public OrderBookSnapshot getOrderBook(@PathVariable String symbol) {
    return new OrderBookSnapshotBuilder()
            .setSymbol(symbol)
            .setBestPrices(book.getBestBid(), book.getBestAsk())
            .aggregateBids(book.getBids())
            .aggregateAsks(book.getAsks())
            .build();
}
```

## 🔧 Technology Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 21
- **Build Tool**: Maven
- **Architecture**: Microservices with REST API
- **Design Patterns**: Strategy, Observer, Builder, Factory

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Running the Application
```bash
# Clone the repository
git clone <repository-url>
cd trading-engine

# Build and run
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` and automatically begin the trading simulation.

### API Endpoints

#### Trading API
```http
# Place Order
POST /api/v1/trading/orders
Content-Type: application/json

{
  "symbol": "BTC-USD",
  "side": "BUY",
  "type": "LIMIT",
  "price": 45000.0,
  "quantity": 1.5,
  "traderId": "trader1"
}

# Cancel Order
DELETE /api/v1/trading/orders/{orderId}

# Get Order Details
GET /api/v1/trading/orders/{orderId}

# Get Trader Orders
GET /api/v1/trading/orders/trader/{traderId}

# Get Active Orders
GET /api/v1/trading/orders/active/{symbol}
```

#### Simulation API
```http
# Get Simulation Agents
GET /api/simulation/agents

# Get Simulation Status
GET /api/simulation/status

# Get Trades for Symbol
GET /api/simulation/trades/{symbol}

# Restart Simulation
POST /api/simulation/restart
```

#### Order Book API
```http
GET /api/orderbook/{symbol}
```

#### WebSocket Endpoints
```javascript
// Connect for real-time updates
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Subscribe to trades
stompClient.subscribe('/topic/trades', callback);

// Subscribe to order book updates
stompClient.subscribe('/topic/orderbook/{symbol}', callback);
```

## 📊 Simulation Features

The application automatically starts with 8 trading agents:
- **Alice-Retail**: Retail trader for AAPL
- **Bob-Whale**: Large volume trader for BTC-USD
- **Charlie-Noise**: High-frequency trader for AAPL
- **Dave-Arbitrage**: Arbitrage trader for BTC-USD
- **Eve-Momentum**: Momentum trader for AAPL
- **Frank-HFT**: High-frequency trader for BTC-USD
- **Grace-Retail**: Retail trader for BTC-USD
- **Hank-Whale**: Large volume trader for AAPL

Each agent operates independently, placing orders based on their trading strategy and reacting to market data updates.

## � Web Dashboard

The application includes a sophisticated **Aurora Simulation Dashboard** accessible at `http://localhost:8080`:

### Dashboard Features
- **Real-time Order Book**: Live bid/ask spreads with price levels
- **Trade Feed**: Streaming trade executions with price and volume
- **Agent Monitoring**: Track individual agent performance and portfolios
- **Interactive Charts**: Price charts with technical indicators
- **Market Statistics**: Real-time market metrics and analytics
- **WebSocket Integration**: Live updates without page refresh

### Technology Stack
- **Frontend**: HTML5, CSS3, JavaScript with Chart.js
- **Real-time Communication**: STOMP over WebSocket
- **Responsive Design**: Mobile-friendly interface
- **Modern UI**: Dark theme with gradient accents

## �🎓 Educational Value

This project serves as an excellent learning resource for:
- **Design Patterns**: Real-world implementations of Strategy, Observer, and Builder patterns
- **Software Architecture**: Clean separation of concerns and modular design
- **Trading Systems**: Understanding of order book mechanics and matching algorithms
- **Spring Boot**: REST API development and dependency injection
- **Concurrency**: Multi-threaded trading simulation and real-time updates

## 📝 Key Learning Points

1. **Good vs Bad Design**: Explicit comments demonstrate design principle violations and their solutions
2. **GRASP Principles**: Information Expert, Creator, Controller, and Low Coupling patterns
3. **SOLID Principles**: Single Responsibility, Open/Closed, and Dependency Inversion
4. **Design Patterns**: When and how to apply Strategy, Observer, and Builder patterns
5. **API Design**: Clean REST endpoints with proper DTOs and separation of concerns

---

**Note**: This project deliberately includes commented examples of bad design practices alongside good practices to serve as an educational resource for software engineering students and developers learning about design patterns and principles.
