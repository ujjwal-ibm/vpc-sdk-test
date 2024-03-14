package main

import (
	"encoding/json"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/IBM/go-sdk-core/v5/core"
	"github.com/IBM/vpc-go-sdk/vpcv1"
)

func main() {

	// to enable debug logging
	// logDestination := log.Writer()
	// goLogger := log.New(logDestination, "", log.LstdFlags)
	// core.SetLogger(core.NewLogger(core.LevelDebug, goLogger, goLogger))

	// get the api key for IAM authenticator from env
	apiKey := os.Getenv("IC_API_KEY")
	servicename := "vpc"
	region := "eu-de"
	// specify url for region specific
	url := "https://" + region + ".iaas.cloud.ibm.com/v1"

	// default iam url
	iamurl := "https://iam.cloud.ibm.com"
	authenticator := &core.IamAuthenticator{
		ApiKey: apiKey,
		URL:    iamurl,
	}

	// create vpcv1options
	vpcv1opt := vpcv1.VpcV1Options{
		ServiceName:   servicename,
		URL:           url,
		Authenticator: authenticator,
	}

	// create vpc client using vpcv1options
	vpcclient, err := vpcv1.NewVpcV1(&vpcv1opt)

	if err != nil {
		fmt.Println("VPC Service creation failed.", err)
	}

	// create vpc
	vpcName := "test-sdk-vpc"
	createVPCOptions := &vpcv1.CreateVPCOptions{
		Name: &vpcName,
	}
	vpc, res, err := vpcclient.CreateVPC(createVPCOptions)
	if err != nil {
		fmt.Printf("VPC creation failed.%s/n%v", err, res)
	}
	fmt.Printf("VPC created : %s\n", *vpc.ID)

	zone := region + "-1"
	subnetName := "test-sdk-sub"
	createSubnetptions := &vpcv1.CreateSubnetOptions{
		SubnetPrototype: &vpcv1.SubnetPrototypeSubnetByTotalCount{
			Name: &subnetName,
			Zone: &vpcv1.ZoneIdentityByName{
				Name: &zone,
			},
			TotalIpv4AddressCount: core.Int64Ptr(64),
			VPC: &vpcv1.VPCIdentityByID{
				ID: vpc.ID,
			},
		},
	}
	subnet, res, err := vpcclient.CreateSubnet(createSubnetptions)
	if err != nil {
		fmt.Printf("Subnet creation failed.%s/n%v", err, res)
	}
	fmt.Printf("Subnet created : %s\n", *subnet.ID)

	pubsshkey := "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC8kvMw4LpqoLAxmhS1NreiQ9pxrvPP0c8qyIjj5QS4neLdgFYZjYz1ZyyOODgqt4LmSJOTPEoZU4oKG6Yj1FcHavUd6le1UTGwveyT+2/l8Z71qeSX6P30klk4XNOWw0TZZ8/PVwK8E3mn6xu7YsAXfqHCKFZ1wu4p8DXacZCiBFWkNIxg9YP89s/ulah2A/pINlzXqfTSfL0f0thYpVKdH4J5AMj1r53igiqbIfYuyiBw2oIvCF7zdV1c9u/c2EUq6VDG+0jHAv10XtKnC4u+r/I3AmBxODlXLHTqAsRC+yRDVyJwyFcy3Uz1Cd2YEeMBIaGZQZqmBiah27hlGmIg3MizVHsMjbgL7aHRsOEyLgi7A4lq4RUp3l0I7Lan7VDs9A9zgEgcj0WNzo3kKctxaYhNoe6RXDmtjFtGbFx2HqK5IPm/6sCpaFMSeCCTSrCsl7SIYFmv5om8i+zGqI938KHUy4kAWJ33Glg72SpoR5ZMXEpmHhwZ3gFCYxOH8QYSlNEGSaWXSp1E3XWm8TWav/1FjGExaoiudq0m2qI4JSZxL44kVDnIrGlOUY2iiuvxZLS5rixi2gAVMtajObajADEWjAZyERaLkcgSwEB3ux02m4ny+Va8VE4zHJxhTxPioppgmJ6Z3MSBj+QwdvSvRogOo+VrfpRQUsolRz2+3Q=="
	keyName := "test-sdk-ssh"
	createKeyptions := &vpcv1.CreateKeyOptions{
		Name:      &keyName,
		PublicKey: &pubsshkey,
	}
	key, res, err := vpcclient.CreateKey(createKeyptions)
	if err != nil {
		fmt.Printf("Key creation failed.%s/n%v", err, res)
	}
	fmt.Printf("Key created : %s\n", *key.ID)

	imageName := "ibm-centos-7-9-minimal-amd64-12"
	listImageOptions := &vpcv1.ListImagesOptions{
		Name: &imageName,
	}
	images, res, err := vpcclient.ListImages(listImageOptions)
	if err != nil {
		fmt.Printf("List images failed.%s/n%v", err, res)
	}
	imageId := images.Images[0].ID
	profile := "bx2-2x8"
	instanceName := "ujjwal-vsi"

	instancePrototypeInstanceByImage := &vpcv1.InstancePrototypeInstanceByImage{
		Image: &vpcv1.ImageIdentityByID{
			ID: imageId,
		},
		Zone: &vpcv1.ZoneIdentity{
			Name: &zone,
		},
		Profile: &vpcv1.InstanceProfileIdentity{
			Name: &profile,
		},
		Name: &instanceName,
		VPC: &vpcv1.VPCIdentity{
			ID: vpc.ID,
		},
		PrimaryNetworkInterface: &vpcv1.NetworkInterfacePrototype{
			Subnet: &vpcv1.SubnetIdentity{
				ID: subnet.ID,
			},
		},
		UserData: core.StringPtr(getuserdataAsStringFromFile("userdata.sh")),
	}

	keyobjs := make([]vpcv1.KeyIdentityIntf, 0)
	keyobj := &vpcv1.KeyIdentity{
		ID: key.ID,
	}
	keyobjs = append(keyobjs, keyobj)

	instancePrototypeInstanceByImage.Keys = keyobjs

	createInstanceOptions := &vpcv1.CreateInstanceOptions{
		InstancePrototype: instancePrototypeInstanceByImage,
	}
	vsi_created, res, err := vpcclient.CreateInstance(createInstanceOptions)
	fmt.Printf("VSI created : %s\n", *vsi_created.ID)

	if err != nil {
		log.Printf("Error creating VSI. %s/n%s", err, res)
	} else {
		printJson(vsi_created)
	}

	// expecting vsi to take 10-20 seconds to come up

	time.Sleep(30 * time.Second)

	// delete vsi

	deleteInstanceOptions := &vpcv1.DeleteInstanceOptions{
		ID: vsi_created.ID,
	}
	res, err = vpcclient.DeleteInstance(deleteInstanceOptions)
	if err != nil {
		log.Printf("Error deleting VSI. %s/n%s", err, res)
	}
	fmt.Printf("vsi deleted : %s\n", *vsi_created.ID)

	// expecting vsi to take 10-20 seconds to delete

	time.Sleep(30 * time.Second)

	// delete key
	deleteKeyOptions := &vpcv1.DeleteKeyOptions{
		ID: key.ID,
	}
	res, err = vpcclient.DeleteKey(deleteKeyOptions)
	if err != nil {
		log.Printf("Error deleting key. %s/n%s", err, res)
	}
	fmt.Printf("key deleted : %s\n", *key.ID)

	// delete subnet
	deleteSubnetOptions := &vpcv1.DeleteSubnetOptions{
		ID: subnet.ID,
	}
	res, err = vpcclient.DeleteSubnet(deleteSubnetOptions)
	if err != nil {
		log.Printf("Error deleting subnet. %s/n%s", err, res)
	}
	fmt.Printf("Subnet deleted : %s\n", *subnet.ID)

	// expecting subnet to take 10 seconds to delete

	time.Sleep(10 * time.Second)

	// delete vpc
	deleteVpcOptions := &vpcv1.DeleteVPCOptions{
		ID: vpc.ID,
	}
	res, err = vpcclient.DeleteVPC(deleteVpcOptions)
	if err != nil {
		log.Printf("Error deleting vpc. %s/n%s", err, res)
	}
	fmt.Printf("VPC deleted : %s\n", *vpc.ID)

}

func printJson(input interface{}) {
	output, _ := json.MarshalIndent(input, "", "    ")
	log.Println(string(output))
}

func getuserdataAsStringFromFile(filename string) string {
	userdatafile, err := os.ReadFile(filename) // reading the file
	if err != nil {
		fmt.Print(err)
	}
	return string(userdatafile) // convert content to a 'string'
}
