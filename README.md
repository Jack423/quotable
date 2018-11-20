# Quotable

### Version

1.5

### Getting Started

Please read these instructions if you are setting up this project for the first time on your machine.

### Prerequisites

You will need a few different things to get this running. Make sure you have the latest version of Android Studio working on your machine.

* Android Studio
* Git
* Node.js
* Virtual Environment to test app
* Java JDK

### Setting up Node.js

This application uses Firebase Cloud Functions which handles all of the opperations for sending notifications for various tasks inside the app. Make sure that you go online and download the latest version of Node.js from their website.

```cd``` To the location of where the project was cloned or where it is stored.
```npm install -g firebase-tools```

Accept any prompts asking for dependencies or other packages

Run

```
firebase login
```

```
firebase init
```

Then select the project you would like to link, then select the quotable project. If you don't see this option, then you probably don't have the project shared with you.

### Opening Project in Android Studio

This should be pretty self explanitory, but you may need to install any build tools. This will show up inside the console window in Android Studio if your build tools are not installed properly. Be patient with this as it might take several minues to get all of the build tools installed on your machine.

### Author

* Jack Butler

### Acknowledgments

* Rozdoum - Providing me with some core ideas on how to build the social media and database aspect of Quotable


