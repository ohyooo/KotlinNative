# UI Module MVI Architecture

本文档描述 `ui` 模块当前的 MVI 数据流。图表使用 Markdown 常见支持的 Mermaid 语法，可在 GitHub、JetBrains IDE Markdown 预览等环境中渲染。

## Component Flow

```mermaid
flowchart TD
    AndroidApp[Android MainActivity] --> SharedApp[SharedApp]
    IOSApp[iOS SwiftUI ContentView] --> SharedUIBridge[SharedUIBridge]
    SharedUIBridge --> SharedApp

    SharedApp --> Store[SharedStore]
    Store --> StateFlow["StateFlow&lt;SharedUiState&gt;"]
    StateFlow --> SharedApp
    SharedApp --> SharedScreen[SharedScreen]

    SharedScreen --> Intent[SharedIntent]
    Intent --> Store

    Store --> Repository[SharedRepository]
    Repository --> Loader[SharedDataLoader]
    Loader --> PlatformBridge[Platform / shared module loader]

    Repository --> Result[SharedLoadResult]
    Result --> Action[SharedAction]
    Action --> Reducer[SharedReducer]
    Reducer --> State[SharedUiState]
    State --> StateFlow

    Store --> EffectFlow["SharedFlow&lt;SharedEffect&gt;"]
    EffectFlow --> SharedApp
    SharedApp --> Snackbar[SnackbarHost]
    Snackbar --> ErrorShown[SharedIntent.ErrorShown]
    ErrorShown --> Store
```

## Load Interaction

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Screen as SharedScreen
    participant App as SharedApp
    participant Store as SharedStore
    participant Repository as SharedRepository
    participant Loader as SharedDataLoader
    participant Reducer as SharedReducer

    User->>Screen: Tap Load
    Screen->>Store: dispatch(LoadClicked)
    Store->>Reducer: LoadingStarted
    Reducer-->>Store: state.copy(isLoading = true)
    Store-->>App: state: SharedUiState
    App-->>Screen: render loading state

    Store->>Repository: load()
    Repository->>Loader: loadStatus()
    Repository->>Loader: loadContent()

    alt load succeeds
        Repository-->>Store: Success(SharedData)
        Store->>Reducer: LoadingSucceeded(data)
        Reducer-->>Store: state with status and content
        Store-->>App: state: SharedUiState
        App-->>Screen: render content
    else load fails
        Repository-->>Store: Failure(message)
        Store->>Reducer: LoadingFailed(message)
        Reducer-->>Store: state with errorMessage
        Store-->>App: state: SharedUiState
        Store-->>App: effect: ShowError(message)
        App-->>Screen: render error state
        App-->>User: show snackbar
        App->>Store: dispatch(ErrorShown)
        Store->>Reducer: ErrorConsumed
        Reducer-->>Store: state.copy(errorMessage = null)
    end
```

## Roles

- `SharedUiState`: 页面可持续渲染的状态，包括 `status`、`content`、`isLoading`、`errorMessage`。
- `SharedIntent`: UI 输入事件，目前包括 `LoadClicked` 和 `ErrorShown`。
- `SharedAction`: Store 内部交给 Reducer 的状态变更命令。
- `SharedReducer`: 纯状态转换逻辑，负责根据 `SharedAction` 生成新的 `SharedUiState`。
- `SharedEffect`: 一次性副作用事件，目前用于展示错误 Snackbar。
- `SharedStore`: MVI 中枢，接收 Intent、调用 Repository、触发 Reducer、发布 State 和 Effect。
- `SharedRepository`: 将 `SharedDataLoader` 的平台/共享数据加载结果包装为 `SharedLoadResult`。
- `SharedScreen`: 只消费 `SharedUiState` 并通过回调上报 `SharedIntent`。
