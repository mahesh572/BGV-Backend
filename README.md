   ┌──────────────┐
                │ Action API   │
                └──────┬───────┘
                       ↓
               ActionCreatedEvent
                       ↓
        ┌──────────────┼──────────────┐
        ↓              ↓              ↓
 DocumentUpdater   CheckUpdater   NotificationService
        ↓              ↓              ↓
 DocumentUpdated   CheckStatus   Email / Push
      Event          Event
           ↓
     SyncService
