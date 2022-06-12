/**

  Trabalho realizado por Gonçalo Lourenço n.55780
                          Joana Faria n.55754

*/

#include <time.h>
#include <TimeLib.h>

#define DEVICE_ID "device2"
#define START_NAME "device2_"
#define END_NAME ".jpg"
#define FB_DOOR "/devices/device2/openDoor"
#define FB_LIGHT "/devices/device2/ledOn"
#define FB_MESSAGE1 "/devices/device2/messageOnDisplay1"
#define FB_MESSAGE2 "/devices/device2/messageOnDisplay2"
#define FB_OCCURRENCES "/devices/device2/occurrences/"
#define AUX "/devices/device2/occurrences/id0/deviceId"

//variables
int light = 0;
char message1[16] = "linha de cima";
char message2[16] = "linha de baixo";
bool openOrder = 0;
int occurrenceCounter = 11;

//char filePhoto[] = START_NAME;


//Camara
#include "WiFi.h"
#include "esp_camera.h"
#include "Arduino.h"
#include "soc/soc.h"           // Disable brownout problems
#include "soc/rtc_cntl_reg.h"  // Disable brownout problems
#include "driver/rtc_io.h"
#include <SPIFFS.h>
#include <FS.h>
#include <Firebase_ESP_Client.h>
//Provide the token generation process info.
#include <addons/TokenHelper.h>

//network credentials
const char* ssid = "ZON-1360";
const char* password = "404cef88d91b";

// Insert Firebase project API Key
#define API_KEY "AIzaSyDrTC_1aYKKDV3w_OdbJO9md3B2kquZbUg"

// Insert Authorized Email and Corresponding Password
#define USER_EMAIL "goncalomarlourenco@gmail.com"
#define USER_PASSWORD "oioioi"

// Insert Firebase databaseURL
#define DATABASE_URL "campainhasmart-default-rtdb.europe-west1.firebasedatabase.app"

// Insert Firebase storage bucket ID
#define STORAGE_BUCKET_ID "campainhasmart.appspot.com"

// Photo File Name to save in SPIFFS
//char FILE_PHOTO[] = "/doobell1.jpg";

// camera module pins (CAMERA_MODEL_AI_THINKER)
//#define CAMERA_MODEL_WROVER_KIT // Has PSRAM
//#include "camera_pins.h"
#define PWDN_GPIO_NUM     -1
#define RESET_GPIO_NUM    -1
#define XCLK_GPIO_NUM     21
#define SIOD_GPIO_NUM     26
#define SIOC_GPIO_NUM     27

#define Y9_GPIO_NUM       35
#define Y8_GPIO_NUM       34
#define Y7_GPIO_NUM       39
#define Y6_GPIO_NUM       36
#define Y5_GPIO_NUM       19
#define Y4_GPIO_NUM       18
#define Y3_GPIO_NUM        5
#define Y2_GPIO_NUM        4
#define VSYNC_GPIO_NUM    25
#define HREF_GPIO_NUM     23
#define PCLK_GPIO_NUM     22

//Define Firebase Data objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig configF;

// Check if photo capture was successful
bool checkPhoto( fs::FS &fs , String filePhoto) {
  File f_pic = fs.open( filePhoto );
  unsigned int pic_sz = f_pic.size();
  return ( pic_sz > 100 );
}

// Capture Photo and Save it to SPIFFS
bool capturePhotoSaveSpiffs(String filePhoto) {
  camera_fb_t * fb = NULL; // pointer
  bool ok = 0; // Boolean indicating if the picture has been taken correctly
  
  int i = 0;
  while(!ok && i < 10) {
    // Take a photo with the camera
    Serial.println("Taking a photo...");

    fb = esp_camera_fb_get();
    if (!fb) {
      Serial.println("Camera capture failed");
      return false;
    }

    // Photo file name
    //String name = START_NAME + String(occurrenceCounter) + END_NAME;
    //filePhoto = name;

    Serial.printf("Picture file name: %s\n", filePhoto);
    File file = SPIFFS.open(filePhoto, FILE_WRITE);
    // Insert the data in the photo file
    if (!file) {
      Serial.println("Failed to open file in writing mode");
    }
    else {
      file.write(fb->buf, fb->len); // payload (image), payload length
      Serial.print("The picture has been saved in ");
      Serial.print(filePhoto);
      Serial.print(" - Size: ");
      Serial.print(file.size());
      Serial.println(" bytes");
    }
    // Close the file
    file.close();
    esp_camera_fb_return(fb);

    //Número max de tentativas
    i+=1;
    Serial.println(i);
    Serial.println("Tentativa de escrever a imagem");
    Serial.println();

    // check if file has been correctly saved in SPIFFS
    ok = checkPhoto(SPIFFS, filePhoto);
  } 
  return ok;
}

void initWiFi(bool tryAgain){
  WiFi.begin(ssid, password);
  //while (WiFi.status() != WL_CONNECTED) {
  while (WiFi.isConnected() != true && tryAgain) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
}

void initSPIFFS(){
  if (!SPIFFS.begin(true)) {
    Serial.println("An Error has occurred while mounting SPIFFS");
    ESP.restart();
  }
  else {
    delay(500);
    Serial.println("SPIFFS mounted successfully");
  }
}

void initCamera(){
 // OV2640 camera module
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sscb_sda = SIOD_GPIO_NUM;
  config.pin_sscb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;

  Serial.println("initCamera before if");

  if (psramFound()) {
    config.frame_size = FRAMESIZE_UXGA;
    config.jpeg_quality = 10;
    config.fb_count = 2;
  } else {
    config.frame_size = FRAMESIZE_SVGA;
    config.jpeg_quality = 12;
    config.fb_count = 1;
  }

  Serial.println("initCamera before esp_camera_init");
  //apagar
  digitalWrite(PWDN_GPIO_NUM, LOW);
  delay(10);
  digitalWrite(PWDN_GPIO_NUM, HIGH);
  delay(10);

  // Camera init
  esp_err_t err = esp_camera_init(&config);
  Serial.println(err == ESP_OK);

  if (err != ESP_OK) {
    Serial.printf("Camera init failed with error 0x%x", err);
    ESP.restart();
  } 
}



//Photoresistor
// constants
const int sensorPin = 5;    // pin that the sensor is attached to
// photoresistor variables:
int sensorValue = 0;         // the sensor value
int sensorMin = 10230;        // minimum sensor value
int sensorMax = 0;           // maximum sensor value


//Servo
#include <ESP32Servo.h>
Servo myservo;  // create servo object to control a servo
int posVal = 0;    // variable to store the servo position
int servoPin = 15; // Servo motor pin


//Doorbell
#define PIN_BUZZER 12
#define PIN_BUTTON 0


//LCD
#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#define SDA 13                    //Define SDA pins
#define SCL 14                    //Define SCL pins
LiquidCrystal_I2C lcd(0x27,16,2); 


//Movement Sensor
int movementSensorPin = 33; // the number of the infrared motion sensor pin
int movement = 0;

//LED
int lightPin = 32;    // the number of the LED pin





void setup() {
  Serial.begin(115200);
  setTime(20,0,0,11,6,2022);

  //Servo
  myservo.setPeriodHertz(50);           // standard 50 hz servo
  myservo.attach(servoPin, 500, 2500);  // attaches the servo on servoPin to the servo object
  myservo.write(0);

  //DoorBell
  pinMode(PIN_BUZZER, OUTPUT);
  pinMode(PIN_BUTTON, INPUT);

  //LCD
  Wire.begin(SDA, SCL);           // attach the IIC pin
  lcd.init();                     // LCD driver initialization
  lcd.backlight();                // Open the backlight
  lcd.setCursor(0,0);             // Move the cursor to row 0, column 0
  lcd.print("hello, world!");     // The print content is displayed on the LCD

  //Movement Sensor
  pinMode(movementSensorPin, INPUT);  // initialize the sensor pin as input

  //LED
  pinMode(lightPin, OUTPUT);    // initialize the LED pin as output

  //photoresistorSetup();

  initWiFi(true);

  camaraSetup();
}

void photoresistorSetup() {
  Serial.println("Begin photoresistor calibration");

  //pinMode(sensorPin, INPUT);

  //Serial.begin(115200);
  // turn on LED to signal the start of the calibration period:
  digitalWrite(lightPin, HIGH);

  // calibrate during the first five seconds
  while (millis() < 5000) {
    sensorValue = analogRead(sensorPin);
    // record the maximum sensor value
    if (sensorValue > sensorMax) {
      sensorMax = sensorValue;
    }
    // record the minimum sensor value
    if (sensorValue < sensorMin) {
      sensorMin = sensorValue;
    }
  }
  Serial.println("End photoresistor calibration");

  // signal the end of the calibration period
  digitalWrite(lightPin, LOW);
}

void camaraSetup(){
  Serial.println("camara setup begin");
  // Serial port for debugging purposes
  //Serial.begin(115200);
  
  initSPIFFS();
  // Turn-off the 'brownout detector'
  WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0);
  Serial.println("before camera");
  initCamera();
  Serial.println("after camera");
  

  //Firebase
  // Assign the api key
  configF.api_key = API_KEY;
  configF.database_url = DATABASE_URL;

  //Assign the user sign in credentials
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  //Assign the callback function for the long running token generation task
  configF.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  Firebase.begin(&configF, &auth);
  Firebase.reconnectWiFi(true);
}





void loop() {

  if(WiFi.isConnected() != true) {
    initWiFi(false);
  }

  if(!Firebase.ready()){
    Firebase.begin(&configF, &auth);
    Firebase.reconnectWiFi(true);
  }



  if(Firebase.RTDB.getInt(&fbdo, FB_DOOR)){
    openOrder = fbdo.intData();
  } else {
    Serial.println(fbdo.errorReason());
  }
  if(openOrder){
    servo();
    if(Firebase.RTDB.setInt(&fbdo, FB_DOOR, 0)){
      Serial.println("Door ready to close");
    } else {
      Serial.println(fbdo.errorReason());
    }
  }
  
  
  //Serial.println("fb light" + light);
  if(Firebase.RTDB.getInt(&fbdo, FB_LIGHT)){
    light = fbdo.intData();
    Serial.println("fb light" + String(light));
    if(light>0){
      digitalWrite(lightPin, light);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }

  
  
  LCD();


  //photoresistor();

  bool ring = doorbell();

  Serial.println(ring);
  if(ring) {
    Serial.println("doorbell rang");
    occurrence("DOORBELL");
  } else {
    // movement sensor options
    movement = digitalRead(movementSensorPin);
    if(movement > 0){
      occurrence("MOVEMENT");
      digitalWrite(lightPin, movement);
    } else {
      digitalWrite(lightPin, light);
    }
  }

  //endCicle
  delay(1000);              // wait for a small time
}

void servo(){
  
    for (posVal = 0; posVal <= 180; posVal += 1) { // goes from 0 degrees to 180 degrees
      // in steps of 1 degree
      myservo.write(posVal);       // tell servo to go to position in variable 'pos'
      delay(15);                   // waits 15ms for the servo to reach the position
    }
    for (posVal = 180; posVal >= 0; posVal -= 1) { // goes from 180 degrees to 0 degrees
      myservo.write(posVal);       // tell servo to go to position in variable 'pos'
      delay(15);                   // waits 15ms for the servo to reach the position
    }
  
}

bool doorbell(){
  int bellpress = 0;
  for(int i = 0; i < 10; i++){
    if(digitalRead(PIN_BUTTON) == LOW){bellpress += 1;Serial.println("button low");} //botão high or low
  }
  Serial.println(bellpress);
  if (bellpress > 7) {
    digitalWrite(PIN_BUZZER,HIGH);
    return true;
  } else {
    digitalWrite(PIN_BUZZER,LOW);
    return false;
  }
}

void LCD(){

  String m1 = "";
  String m2 = "";
  if(Firebase.RTDB.getInt(&fbdo, FB_MESSAGE1)){
    m1 = fbdo.stringData();
  } else {
    Serial.println(fbdo.errorReason());
  }
  if(Firebase.RTDB.getString(&fbdo, FB_MESSAGE2)){
    m2 = fbdo.stringData();
  } else {
    Serial.println(fbdo.errorReason());
  }

  lcd.setCursor(0,0);             // Move the cursor to row 1, column 0
  lcd.print(m1+"                    ");          // The count is displayed every second

  lcd.setCursor(0,1);
  lcd.print(m2+"                    ");
}

void photoresistor(){
    // read the sensor:
  sensorValue = analogRead(sensorPin);
  Serial.println();
  Serial.println(sensorMin);
  Serial.println(sensorMax);
  Serial.println(sensorValue);
  Serial.println();

  // in case the sensor value is outside the range seen during calibration
  sensorValue = constrain(sensorValue, sensorMin, sensorMax);

  // apply the calibration to the sensor reading
  sensorValue = map(sensorValue, sensorMin, sensorMax, 0, 255);

  Serial.println(sensorValue);

  // fade the LED using the calibrated value:
  //analogWrite(lightPin, sensorValue);

}

void takePicture(String filePhoto){
  delay(3000);

  Serial.println("going to take pic");
  bool picSuccess = capturePhotoSaveSpiffs(filePhoto);
  if(!picSuccess){
    return;
  }

  Serial.println("pic took");
  
  delay(1);
  
  //Serial.println(Firebase.ready());
  if (Firebase.ready()){
    Serial.print("Uploading picture... ");

    //MIME type should be valid to avoid the download problem.
    //The file systems for flash and SD/SDMMC can be changed in FirebaseFS.h.
    if (Firebase.Storage.upload(&fbdo, STORAGE_BUCKET_ID /* Firebase Storage bucket id */, filePhoto /* path to local file */, mem_storage_type_flash /* memory storage type, mem_storage_type_flash and mem_storage_type_sd */, filePhoto /* path of remote file stored in the bucket */, "image/jpeg" /* mime type */)){
      Serial.printf("\nDownload URL: %s\n", fbdo.downloadURL().c_str());
    }
    else{
      Serial.println(fbdo.errorReason());
    }
  }

  
  Serial.print("Uploaded picture... ");
}

void occurrence(String type){
  long date = now();
  String id = START_NAME + String(occurrenceCounter);
  String photoName = START_NAME + String(occurrenceCounter) + END_NAME;
  String filePhoto = "/" + photoName;

  String datePath = FB_OCCURRENCES + id + "/date";
  String deviceIdPath = FB_OCCURRENCES + id + "/deviceId";
  String idPath = FB_OCCURRENCES + id + "/id";
  String photoPath = FB_OCCURRENCES + id + "/photo";
  String typePath = FB_OCCURRENCES + id + "/type";


  if(Firebase.RTDB.setFloat(&fbdo, datePath, date)){
    Serial.println("new occurrence date added");
    //openOrder = false;    
  } else {
    Serial.println(fbdo.errorReason());
  }
  if(Firebase.RTDB.setString(&fbdo, deviceIdPath, DEVICE_ID)){
    Serial.println("new occurrence deviceId added");
  } else {
    Serial.println(fbdo.errorReason());
  }
  if(Firebase.RTDB.setString(&fbdo, idPath, id)){
    Serial.println("new occurrence id added");
  } else {
    Serial.println(fbdo.errorReason());
  }
  if(Firebase.RTDB.setString(&fbdo, photoPath, photoName)){
    Serial.println("new occurrence photo path added");
  } else {
    Serial.println(fbdo.errorReason());
  }
  if(Firebase.RTDB.setString(&fbdo, typePath, type)){
    Serial.println("new occurrence type added");
  } else {
    Serial.println(fbdo.errorReason());
  }

  occurrenceCounter +=1;

  takePicture(filePhoto);


}