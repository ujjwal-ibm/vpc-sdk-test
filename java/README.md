# vpc-sdk-test
To use the code in App.java, export your IAM api key in below format
```
export IC_API_KEY=<your api key>
```

expected output

```
createVpc() :
vpc created : r010-3d5b9f0e-ad86-4141-8806-c4db86e7bc48

createSubnet() :
subnet created :02b7-8d785f2a-01e3-47bf-a769-f1eed4c5741c

createKey() :
key created :r010-49c4548a-1d40-403b-b692-7ac6a5a3e6fb

listImages() :
createInstance() :
vsi created :02b7_65ddda66-945d-493a-8a91-b011be2ad5db

vsi deleted :02b7_65ddda66-945d-493a-8a91-b011be2ad5db

deleteInstance() response status code: 204
key deleted :r010-49c4548a-1d40-403b-b692-7ac6a5a3e6fb

deleteKey() response status code: 204
subnet deleted :02b7-8d785f2a-01e3-47bf-a769-f1eed4c5741c

deleteSubnet() response status code: 204
vpc deleted :r010-3d5b9f0e-ad86-4141-8806-c4db86e7bc48

deleteVpc() response status code: 204

```