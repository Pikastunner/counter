# Kotlin Counter App

This Kotlin Counter App is a simple application that allows users to increment, decrement, and reset a counter. It's designed for simplicity and ease of use.

## Features

- **Increment Counter**: Increase the counter value by one.
- **Decrement Counter**: Decrease the counter value by one.
- **Reset Counter**: Reset the counter value to zero.

## Implementation

### MainActivity.kt

The `MainActivity.kt` file contains the main logic of the application. Here's a brief overview of its components:

- **Buttons**: `add_btn`, `minus_btn`, and `reset` buttons are used to perform corresponding actions on the counter.
- **TextView**: `txv` displays the current value of the counter.
- **SharedPreferences**: Used to store and retrieve the counter value persistently.
- **Navigation Drawer**: Provides access to additional functionalities such as creating new counters and accessing settings.

### SettingsFragment.kt

The `SettingsFragment.kt` file defines the settings fragment where users can customize app preferences.

## Usage

1. **Increment**: Tap the "+" button to increase the counter value.
2. **Decrement**: Tap the "-" button to decrease the counter value.
3. **Reset**: Tap the "Reset" button to set the counter value to zero.
4. **Settings**: Access the settings fragment from the navigation drawer to customize preferences.

## Contributing

Contributions are welcome! If you have any suggestions or improvements, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
