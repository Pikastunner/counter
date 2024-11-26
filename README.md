# Kotlin Counter App v1.1

The Kotlin Counter App has been updated to version **1.1**, introducing **widget functionality** to enhance the user experience. This update allows users to interact with their counters directly from their device's home screen. The core functionality of the app remains intact, with the addition of a responsive and user-friendly widget.

---

## New Features in v1.1

### **Widget Functionality**

- **Home Screen Widget**:
  - Users can add a widget to their home screen that displays a selected counter.
  - The widget shows the counter's name and value in real-time.

- **Widget Actions**:
  - **Increment Button**: Increment the counter value directly from the widget.
  - **Decrement Button**: Decrease the counter value from the widget.
  - **Cycle Counters**: Use navigation buttons to switch between different counters linked to the widget.

- **Customizable**:
  - Users can select which counter to display in the widget.
  - The widget updates automatically when counter values are modified in the app.

---

## Features

- **Core App Features** (unchanged from v1.0):
  - Create, increment, decrement, reset, and delete counters.
  - Persistent storage using `SharedPreferences`.
  - Navigation drawer for managing counters and accessing settings.

- **New Widget Features** (v1.1):
  - A responsive home screen widget.
  - Widget-specific actions like increment, decrement, and counter cycling.
  - Seamless synchronization with the app's counters.

---

## Implementation Details

### Widget Provider (`CounterWidgetProvider.kt`)

- The `CounterWidgetProvider` class extends `AppWidgetProvider` and handles widget updates and user interactions.
- Key functionalities include:
  - **Update Widget**: Dynamically updates the counter name and value displayed on the widget.
  - **Handle Actions**: Listens for user interactions (e.g., increment, decrement, or switching counters) and updates the widget accordingly.

### Widget Layout (`res/layout/widget_layout.xml`)

- The widget layout includes:
  - TextView to display the counter name and value.
  - Buttons for incrementing, decrementing, and switching counters.

### Integration with MainActivity

- **Broadcast Intents**:
  - The widget communicates with the main app using broadcast intents.
  - When the user interacts with the widget, the app processes the action and updates the widget's state.

---

## Usage

1. **Add Widget to Home Screen**:
  - Long-press on the home screen, select "Widgets," and add the Counter Widget.

2. **Select Counter for Widget**:
  - When adding the widget, select the counter to display from the list.

3. **Interact with Widget**:
  - Use the "+" and "-" buttons to modify the counter value.
  - Cycle through available counters using the navigation buttons.

4. **Update Counters in App**:
  - Changes made via the widget are reflected in the app, and vice versa.

---

## Contributing

Contributions are welcome! If you have ideas for further improvements or encounter any issues, feel free to open a pull request or issue.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
