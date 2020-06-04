Anti-COVID
===

Project Intro
---

Due to the current pandemic, people have been asked to maintain social distancing in public space. Our team initiated an idea of developing a simple walk in clinic App to achieve the non-contact check-in process

Basic Feature and Functionality
---

Using the patient's location makes sure he is physically at the hospital.
Patients can check-in by App & Arduino device, instead of contacting a real person.
Arduino will make sound to remind patients of the time of meeting doctors.
Arduino will send messages to the App that can call his name & room number to meet the doctors.


Interactive End
---

- **Mobile End**: Patients login, pay, waiting and notification.
	- Android + Kotlin + GPS + BLE
- **Arduino**: Ring to notify.
- **Frontend**: Doctor manage patients
	- React + Bootstrap
- **Database**: Maintaining patient queue and whole information.
	- Firebase
- **Server End**: Supporting commnication bewteen webpage and database.
	- Apache + PHP


Running Instruction
===

Webpage
---

We use React and bootstrap to build the frontend project. 
The React repo is: https://github.com/changer666666/Anti-COVID-React 
Run the React project and you can see the webpage.

1. Clone the repo
```bash
git clone https://github.com/changer666666/Anti-COVID-React.git
```

2. Download npm:
```bash
https://www.npmjs.com/get-npm
```

3. Install react-bootstrap
```bash
npm install react-bootstrap bootstrap
```

4. Run the React project
```bash
npm start
```

Server
---

We use Apache and php to build the server. 
The Server code is in: https://github.com/changer666666/Anti-COVID/server

1. Install Apache server on local computer
Download Apache server from https://httpd.apache.org/download.cgi. Rememer to choose the correct Apache version fitting your computer OS.

Unzip the download file. Copy it to your destination folder.

2. Environment setup
Edit the configuration file in Apache folder.

3. Run the Apache service











