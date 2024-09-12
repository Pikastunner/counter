# Kotlin Counter App

This Kotlin Counter App allows users to create multiple counters, increment, decrement, reset, and delete them. Designed for simplicity and ease of use, this app provides a user-friendly experience to manage multiple counters.

## Features

- **Create New Counter**: Users can create multiple counters, each with a unique name and value.
- **Increment Counter**: Increase the value of a selected counter by one.
- **Decrement Counter**: Decrease the value of a selected counter by one.
- **Reset Counter**: Reset the value of the selected counter to zero.
- **Delete Counter**: Remove a counter from the list (except when only one counter remains).
- **Persistent Storage**: All counters and their values are saved using `SharedPreferences`.
- **Navigation Drawer**: Access the list of counters, create new ones, and navigate to the settings.

## Implementation

### MainActivity.kt

The `MainActivity.kt` file contains the primary logic for managing counters. Key components include:

- **Buttons**: 
  - `addBtn`: Increments the counter value.
  - `minusBtn`: Decrements the counter value.
  - `resetBtn`: Resets the counter value to zero.
  - **Delete Functionality**: Users can now delete a counter through a confirmation dialog.
  
- **TextView**: Displays the current value of the selected counter.
- **SharedPreferences**: Saves and retrieves counter values and lists persistently.
- **Navigation Drawer**: Allows navigation between multiple counters and includes a settings option.

### SettingsFragment.kt

The `SettingsFragment.kt` defines a customizable settings screen where users can adjust preferences related to the app's functionality.

## New Feature: Delete Counter

- Users can delete a counter by selecting the "Delete" option from the confirmation dialog. This action removes the counter from the list. If the counter list contains only one item, deletion is prevented to ensure there is always at least one counter available.

## Usage

1. **Create a New Counter**: Use the navigation drawer to create and name a new counter.
2. **Increment**: Tap the "+" button to increase the counter value.
3. **Decrement**: Tap the "-" button to decrease the counter value.
4. **Reset**: Tap the "Reset" button to reset the counter value to zero.
5. **Delete**: Tap the "Delete" button from the menu to remove the current counter (this option is disabled if only one counter remains).
6. **Settings**: Customize app preferences via the settings screen.

## Contributing

Contributions are welcome! If you have suggestions, improvements, or bug fixes, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
