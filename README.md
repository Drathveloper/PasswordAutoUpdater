# PasswordAutoUpdater

Simple command-line tool that will keep your accounts database updated. It will read your email inbox and retrieve all messages matching a subject. Then it will find all "user:" and "password:" lines and put together. Finally will update the database table with new passwords.

The properties file must be placed in the same folder than jar file and its name must be application.properties. It can be changed easily replacing some constants.

The sample_application.properties contains all required fields.
