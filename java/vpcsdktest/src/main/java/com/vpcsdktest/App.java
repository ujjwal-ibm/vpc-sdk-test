package com.vpcsdktest;

import com.ibm.cloud.is.vpc.v1.Vpc;

import com.ibm.cloud.is.vpc.v1.model.*;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    protected App() {
    }

    @SuppressWarnings("checkstyle:methodlength")
    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("IC_API_KEY");
        String region = "eu-de";
        String instanceProfileName = "bx2-2x8";
        Vpc vpcService = new Vpc("vpc",
                new IamAuthenticator.Builder().apikey(apiKey).url("https://iam.cloud.ibm.com").build());
        vpcService.setServiceUrl("https://" + region + ".iaas.cloud.ibm.com/v1");
        // Load up our test-specific config properties.
        VPC vpc = null;
        Subnet subnet = null;
        Image image = null;
        Key key = null;
        Instance instance = null;
        String imageName = "ibm-centos-7-9-minimal-amd64-12";
        String publickKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC8kvMw4LpqoLAxmhS1NreiQ9pxrvPP0c8qyIjj5QS4neLdgFYZjYz1ZyyOODgqt4LmSJOTPEoZU4oKG6Yj1FcHavUd6le1UTGwveyT+2/l8Z71qeSX6P30klk4XNOWw0TZZ8/PVwK8E3mn6xu7YsAXfqHCKFZ1wu4p8DXacZCiBFWkNIxg9YP89s/ulah2A/pINlzXqfTSfL0f0thYpVKdH4J5AMj1r53igiqbIfYuyiBw2oIvCF7zdV1c9u/c2EUq6VDG+0jHAv10XtKnC4u+r/I3AmBxODlXLHTqAsRC+yRDVyJwyFcy3Uz1Cd2YEeMBIaGZQZqmBiah27hlGmIg3MizVHsMjbgL7aHRsOEyLgi7A4lq4RUp3l0I7Lan7VDs9A9zgEgcj0WNzo3kKctxaYhNoe6RXDmtjFtGbFx2HqK5IPm/6sCpaFMSeCCTSrCsl7SIYFmv5om8i+zGqI938KHUy4kAWJ33Glg72SpoR5ZMXEpmHhwZ3gFCYxOH8QYSlNEGSaWXSp1E3XWm8TWav/1FjGExaoiudq0m2qI4JSZxL44kVDnIrGlOUY2iiuvxZLS5rixi2gAVMtajObajADEWjAZyERaLkcgSwEB3ux02m4ny+Va8VE4zHJxhTxPioppgmJ6Z3MSBj+QwdvSvRogOo+VrfpRQUsolRz2+3Q==";

        try {
            System.out.println("createVpc() :");
            // begin-create_vpc
            CreateVpcOptions createVpcOptions = new CreateVpcOptions.Builder()
                    .name("my-vpc")
                    .build();

            Response<VPC> response = vpcService.createVpc(createVpcOptions).execute();
            vpc = response.getResult();
            System.out.println("vpc created : " + vpc.getId() + "\n");

            // end-create_vpc
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            System.out.println("createSubnet() :");
            // begin-create_subnet
            VPCIdentityById vpcIdentityModel = new VPCIdentityById.Builder()
                    .id(vpc.getId())
                    .build();
            ZoneIdentityByName zoneIdentityModel = new ZoneIdentityByName.Builder()
                    .name(region + "-1")
                    .build();
            SubnetPrototypeSubnetByTotalCount subnetPrototypeModel = new SubnetPrototypeSubnetByTotalCount.Builder()
                    .vpc(vpcIdentityModel)
                    .name("my-subnet")
                    .totalIpv4AddressCount(Long.valueOf("256"))
                    .zone(zoneIdentityModel)
                    .build();
            CreateSubnetOptions createSubnetOptions = new CreateSubnetOptions.Builder()
                    .subnetPrototype(subnetPrototypeModel)
                    .build();

            Response<Subnet> response = vpcService.createSubnet(createSubnetOptions).execute();
            subnet = response.getResult();
            System.out.println("subnet created :" + subnet.getId()+ "\n");

            // end-create_subnet
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            System.out.println("createKey() :");
            // begin-create_key
            CreateKeyOptions createKeyOptions = new CreateKeyOptions.Builder()
                    .name("my-key")
                    .publicKey(publickKey)
                    .build();

            Response<Key> response = vpcService.createKey(createKeyOptions).execute();
            key = response.getResult();
            System.out.println("key created :" + key.getId()+ "\n");

            // end-create_key
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }
        try {
            System.out.println("listImages() :");
            // begin-list_images
            ListImagesOptions listImagesOptions = new ListImagesOptions.Builder()
                    .name(imageName)
                    .build();

            ImagesPager pager = new ImagesPager(vpcService, listImagesOptions);
            List<Image> allResults = new ArrayList<>();
            while (pager.hasNext()) {
                List<Image> nextPage = pager.getNext();
                allResults.addAll(nextPage);
            }

            // end-list_images
            image = allResults.get(0);
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            System.out.println("createInstance() :");
            // begin-create_instance
            KeyIdentityById keyIdentityModel = new KeyIdentityById.Builder()
                    .id(key.getId())
                    .build();
            InstanceProfileIdentityByName instanceProfileIdentityModel = new InstanceProfileIdentityByName.Builder()
                    .name(instanceProfileName)
                    .build();
            VPCIdentityById vpcIdentityModel = new VPCIdentityById.Builder()
                    .id(vpc.getId())
                    .build();

            ImageIdentityById imageIdentityModel = new ImageIdentityById.Builder()
                    .id(image.getId())
                    .build();
            SubnetIdentityById subnetIdentityModel = new SubnetIdentityById.Builder()
                    .id(subnet.getId())
                    .build();
            NetworkInterfacePrototype networkInterfacePrototypeModel = new NetworkInterfacePrototype.Builder()
                    .name("my-network-interface")
                    .subnet(subnetIdentityModel)
                    .build();
            ZoneIdentityByName zoneIdentityModel = new ZoneIdentityByName.Builder()
                    .name(region + "-1")
                    .build();
            InstancePrototypeInstanceByImage instancePrototypeModel = new InstancePrototypeInstanceByImage.Builder()
                    .keys(new java.util.ArrayList<KeyIdentity>(java.util.Arrays.asList(keyIdentityModel)))
                    .name("my-instance")
                    .profile(instanceProfileIdentityModel)
                    .vpc(vpcIdentityModel)
                    .image(imageIdentityModel)
                    .primaryNetworkInterface(networkInterfacePrototypeModel)
                    .zone(zoneIdentityModel)
                    .build();
            CreateInstanceOptions createInstanceOptions = new CreateInstanceOptions.Builder()
                    .instancePrototype(instancePrototypeModel)
                    .build();

            Response<Instance> response = vpcService.createInstance(createInstanceOptions).execute();
            instance = response.getResult();
            System.out.println("vsi created :" + instance.getId()+ "\n");

            // wait for 20 seconds to instance to come up

            Thread.sleep(20000);
            // end-create_instance
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            // begin-delete_instance
            DeleteInstanceOptions deleteInstanceOptions = new DeleteInstanceOptions.Builder()
                    .id(instance.getId())
                    .build();

            Response<Void> response = vpcService.deleteInstance(deleteInstanceOptions).execute();
            System.out.println("vsi deleted :" + instance.getId()+ "\n");
            // wait for delete vsi for 30 seconds
            Thread.sleep(30000);
            // end-delete_instance
            System.out.printf("deleteInstance() response status code: %d%n", response.getStatusCode());
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            // begin-delete_key
            DeleteKeyOptions deleteKeyOptions = new DeleteKeyOptions.Builder()
                    .id(key.getId())
                    .build();

            Response<Void> response = vpcService.deleteKey(deleteKeyOptions).execute();
            System.out.println("key deleted :" + key.getId()+ "\n");
            // end-delete_key
            System.out.printf("deleteKey() response status code: %d%n", response.getStatusCode());
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            // begin-delete_subnet
            DeleteSubnetOptions deleteSubnetOptions = new DeleteSubnetOptions.Builder()
                    .id(subnet.getId())
                    .build();

            Response<Void> response = vpcService.deleteSubnet(deleteSubnetOptions).execute();
            System.out.println("subnet deleted :" + subnet.getId()+ "\n");

            // wait for delete subnet for 15 seconds
            Thread.sleep(15000);
            // end-delete_subnet
            System.out.printf("deleteSubnet() response status code: %d%n", response.getStatusCode());
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

        try {
            // begin-delete_vpc
            DeleteVpcOptions deleteVpcOptions = new DeleteVpcOptions.Builder()
                    .id(vpc.getId())
                    .build();

            Response<Void> response = vpcService.deleteVpc(deleteVpcOptions).execute();
            System.out.println("vpc deleted :" + vpc.getId()+ "\n");

            // end-delete_vpc
            System.out.printf("deleteVpc() response status code: %d%n", response.getStatusCode());
        } catch (ServiceResponseException e) {
            logger.error(String.format("Service returned status code %s: %s%nError details: %s",
                    e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
        }

    }
}
