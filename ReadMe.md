This repository helps to give example about how android based online messenger works.


![Screenshot: Launch image](https://github.com/wnuraeni/IntranetMessenger/blob/master/register screen.png)

About
-----

Intranet Messenger is an Android chat application using intranet database server  to communicate with other registered users.

How it works!
-----

Setup local database server using MySQL, sql file is provided in android_message_server folder.

I used my laptop as local server, in order the chat application to connect to the server, the laptop has to be a hotspot. I recommend mHotspot to do this and don't forget to open firewall for port 80.

Server application created using PHP, copy the folder android_message_server to your webroot folder. Let the folder name remain the same because if you change the folder name, you might also have to make changes in the chat application.

Make sure your phone connected to your laptop through wifi. Install the android apk to your phone.After installation finish, the app will run and enter register screen. By default, app and server connect through ip address 192.168.173.1, if you have different ip address on wifi change it on settings menu. Make sure, you set the right ip address if not then you can not start the application.

After registration succeed, you can start chatting with other users.

FYI,I have not implemented server push notification, but application will receive an incoming new message notification if the application is up on screen. Although, users search and pull-up-to refresh have been implemented in this app.


License
-------

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
