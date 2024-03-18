const { IamAuthenticator } = require("ibm-vpc/auth");
const vpcV1 = require("ibm-vpc/vpc/v1");
const region='eu-de'
const options = {
    authenticator: new IamAuthenticator({
        apikey: process.env.IC_API_KEY,
    }),
    serviceUrl: "https://"+region+".iaas.cloud.ibm.com/v1",
};
const vpcService = vpcV1.newInstance(options);

var vpcID, subnetID, keyID, imageID, instanceID;
// create vpc
console.log('Creating VPC')
const vpcparams = {
    name: 'my-vpc',
    classicAccess: false,
};
var response = vpcService.createVpc(vpcparams);
response.then(function(result) {
    vpcID=result.result.id
    console.log('Created VPC '+vpcID)

    // delete vpc
    console.log('Deleting VPC')
    params = {
        id: vpcID,
    };

    response = vpcService.deleteVpc(params);
    response.then(function(result) {
        console.log("vpc deleted")
    })
})


// // create subnet
// console.log('Creating Subnet')
// const vpcIdentityModel = {
//     id: vpcID,
// };

// const zoneIdentityModel = {
//     name: region+'-1',
// };

// const subnetPrototypeModel = {
//     name: 'my-subnet',
//     vpc: vpcIdentityModel,
//     total_ipv4_address_count: 64,
//     zone: zoneIdentityModel,
// };

// const subnetparams = {
//     subnetPrototype: subnetPrototypeModel,
// };

// response = vpcService.createSubnet(subnetparams);
// response.then(function(result) {
//     subnetID=result.result.id
//     console.log('Created subnet '+subnetID)
// })

// // create key
// console.log('Creating Key')
// const keyparams = {
//     publicKey: 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC8kvMw4LpqoLAxmhS1NreiQ9pxrvPP0c8qyIjj5QS4neLdgFYZjYz1ZyyOODgqt4LmSJOTPEoZU4oKG6Yj1FcHavUd6le1UTGwveyT+2/l8Z71qeSX6P30klk4XNOWw0TZZ8/PVwK8E3mn6xu7YsAXfqHCKFZ1wu4p8DXacZCiBFWkNIxg9YP89s/ulah2A/pINlzXqfTSfL0f0thYpVKdH4J5AMj1r53igiqbIfYuyiBw2oIvCF7zdV1c9u/c2EUq6VDG+0jHAv10XtKnC4u+r/I3AmBxODlXLHTqAsRC+yRDVyJwyFcy3Uz1Cd2YEeMBIaGZQZqmBiah27hlGmIg3MizVHsMjbgL7aHRsOEyLgi7A4lq4RUp3l0I7Lan7VDs9A9zgEgcj0WNzo3kKctxaYhNoe6RXDmtjFtGbFx2HqK5IPm/6sCpaFMSeCCTSrCsl7SIYFmv5om8i+zGqI938KHUy4kAWJ33Glg72SpoR5ZMXEpmHhwZ3gFCYxOH8QYSlNEGSaWXSp1E3XWm8TWav/1FjGExaoiudq0m2qI4JSZxL44kVDnIrGlOUY2iiuvxZLS5rixi2gAVMtajObajADEWjAZyERaLkcgSwEB3ux02m4ny+Va8VE4zHJxhTxPioppgmJ6Z3MSBj+QwdvSvRogOo+VrfpRQUsolRz2+3Q==',
//     name: 'my-ssh-key',
// };
// response = vpcService.createKey(keyparams);
// response.then(function(result) {
//     key=result
//     console.log('Created key '+keyID)
// })
// // list images

// console.log('Getting Images')
// const imageparams = {
//     name: 'ibm-centos-7-9-minimal-amd64-12',
// }

// const allResults = [];
// try {
//     const pager = new vpcV1.ImagesPager(vpcService, imageparams);
//     while (pager.hasNext()) {
//         const nextPage = pager.getNext();
//         nextPage.then(function(result) {
//             allResults.push(...result);
//         })
//     }
// } catch (err) {
//     console.warn(err);
// }
// imageID= allResults[0].id
// // create instance

// console.log('Creating VSI')
// const subnetIdentityModel = {
//     id: subnetID,
// };

// const networkInterfacePrototypeModel = {
//     name: 'my-network-interface',
//     subnet: subnetIdentityModel,
// };

// const instanceProfileIdentityModel = {
//     name: 'bx2-2x8',
// };

// const imageIdentityModel = {
//     id: imageID,
// };

// const instancePrototypeModel = {
//     name: 'my-instance',
//     profile: instanceProfileIdentityModel,
//     vpc: vpcIdentityModel,
//     primary_network_interface: networkInterfacePrototypeModel,
//     zone: zoneIdentityModel,
//     image: imageIdentityModel,
// };

// const instanceparams = {
//     instancePrototype: instancePrototypeModel,
// };

// response = vpcService.createInstance(instanceparams);
// response.then(function(result) {
//     instance=result
//     console.log('Created instance '+instanceID)
// })
// new Promise(resolve => setTimeout(resolve, 20000));

// // delete instance
// console.log('Deleting VSI')
// const params = {
//     id: instanceID,
// };

// response = vpcService.deleteInstance(params);
// response.then(function(result) {
//     console.log("vsi deleted")
//     new Promise(resolve => setTimeout(resolve, 20000));
// })

// // delete key
// console.log('Deleting Key')
// params = {
//     id: keyID,
// };

// response = vpcService.deleteKey(params);
// response.then(function(result) {
//     console.log("key deleted")
// })

// // delete subnet
// console.log('Deleting Subnet')
// params = {
//     id: subnetID,
// };
// response = vpcService.deleteSubnet(params);
// response.then(function(result) {
//     new Promise(resolve => setTimeout(resolve, 20000));
//     console.log("subnet deleted")
// })
