# RegUp features - 

# 1. User authentication through email
# 2. Change and recover login credentials
# 3. Email verification
# 4. Submit or update courses if submission is open (for students)
# 5. View and save submitted data to phone's external storage (for faculties)

I developed this application as a final year project of my graduation. Through this application students can submit which courses they wish to take for the next upcoming semester. Also faculties who are supervisors of a particular intake/section can see and save the submitted course data by the students of that intake/section.

This application is made specifically for my university. But with little bit of modifications it can be used for any other university or educational institution. Because I have used Firebase APIs in my project, so for security purpose I have removed the 'google-services.json' file from the project. If you want to use this project then you have to link your own Firebase 'google-services.json' to this project. You can design your Cloud Firestore database according to this project or you can design as you wish and then change the queries according to your own design. For student verification I have used volley http request to an API url provided to me by my varsity authority. This API gives a students identity information as a result. I have removed my API url. If you want to use this function then you have to use your own API url in the java file 'StudentVerificationActivity'.
