# CloudPhoto
## About
The app was made as homework for cloud technologies course.

It can upload and download photos (jpg ,jpeg) to yandex cloud s3 object storage. Albums are supported.
## Usage
Upload photos from folder `path` to album `album`
```
java -jar cloudphoto-0.1-jar-with-dependencies.jar upload -p path -a album
```
Download photos from album `album` to folder `path`
```
java -jar cloudphoto-0.1-jar-with-dependencies.jar download -p path -a album
```
List all albums
```
java -jar cloudphoto-0.1-jar-with-dependencies.jar list
```
List all photos in album `album`
```
java -jar cloudphoto-0.1-jar-with-dependencies.jar list -a album
```
## Building
Firstly, you need installed java 11 and maven on your system.

To build, run this command inside project sources folder, where pom.xml is located.

```mvn package```

After successful build `cloudphoto-0.1-jar-with-dependencies.jar` will be located in `target` folder.
