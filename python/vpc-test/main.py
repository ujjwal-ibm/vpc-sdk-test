import time

from ibm_vpc.vpc_v1 import *
import os
# import logging
# logging.basicConfig(level=logging.DEBUG)

with open("userdata.sh","r") as f:
    userdata = f.read()
region="eu-de"
url ='https://'+region+'.iaas.cloud.ibm.com/v1'
iamkey=os.environ['IC_API_KEY']
os.environ['VPC_URL'] = url
os.environ['VPC_AUTH_TYPE'] = 'iam'
os.environ['VPC_APIKEY'] = iamkey
os.environ['VPC_AUTH_URL'] = 'https://iam.cloud.ibm.com'

# creating a vpc service client
vpcclient = VpcV1.new_instance()

print("creating vpc client")


print('\ncreate_vpc()')
# begin-create_vpc

vpc = vpcclient.create_vpc(
    address_prefix_management="auto",
    classic_access=False,
    name="my-vpc",
).get_result()

# end-create_vpc
print('\ncreate_subnet()')
vpc_identity_model = {}
vpc_identity_model['id'] = vpc["id"]

zone_identity_model = {}
zone_identity_model['name'] = region+'-1'

subnet_prototype_model = {}
subnet_prototype_model['name'] = 'my-subnet'
subnet_prototype_model['vpc'] = vpc_identity_model
subnet_prototype_model['total_ipv4_address_count'] = 64
subnet_prototype_model['zone'] = zone_identity_model

subnet = vpcclient.create_subnet(subnet_prototype=subnet_prototype_model).get_result()

# end-create_subnet

print('\ncreate_key()')
# begin-create_key

key = vpcclient.create_key(
    public_key=
    'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC8kvMw4LpqoLAxmhS1NreiQ9pxrvPP0c8qyIjj5QS4neLdgFYZjYz1ZyyOODgqt4LmSJOTPEoZU4oKG6Yj1FcHavUd6le1UTGwveyT+2/l8Z71qeSX6P30klk4XNOWw0TZZ8/PVwK8E3mn6xu7YsAXfqHCKFZ1wu4p8DXacZCiBFWkNIxg9YP89s/ulah2A/pINlzXqfTSfL0f0thYpVKdH4J5AMj1r53igiqbIfYuyiBw2oIvCF7zdV1c9u/c2EUq6VDG+0jHAv10XtKnC4u+r/I3AmBxODlXLHTqAsRC+yRDVyJwyFcy3Uz1Cd2YEeMBIaGZQZqmBiah27hlGmIg3MizVHsMjbgL7aHRsOEyLgi7A4lq4RUp3l0I7Lan7VDs9A9zgEgcj0WNzo3kKctxaYhNoe6RXDmtjFtGbFx2HqK5IPm/6sCpaFMSeCCTSrCsl7SIYFmv5om8i+zGqI938KHUy4kAWJ33Glg72SpoR5ZMXEpmHhwZ3gFCYxOH8QYSlNEGSaWXSp1E3XWm8TWav/1FjGExaoiudq0m2qI4JSZxL44kVDnIrGlOUY2iiuvxZLS5rixi2gAVMtajObajADEWjAZyERaLkcgSwEB3ux02m4ny+Va8VE4zHJxhTxPioppgmJ6Z3MSBj+QwdvSvRogOo+VrfpRQUsolRz2+3Q==',
    name='my-ssh-key'
).get_result()

# end-create_key
print('\nget_imageId()')
all_results = []
pager = ImagesPager(
    client=vpcclient,
    limit=10,
    name='ibm-centos-7-9-minimal-amd64-12',
)
while pager.has_next():
    next_page = pager.get_next()
    assert next_page is not None
    all_results.extend(next_page)

image_id=all_results[0]['id']
print('\ncreateVSI()')
profile='bx2-2x8'

volume_profile_identity_model = {}
volume_profile_identity_model['name'] = 'general-purpose'



subnet_identity_model = {}
subnet_identity_model['id'] = subnet['id']


volume_prototype_instance_by_image_context_model = {}
volume_prototype_instance_by_image_context_model['capacity'] = 100
volume_prototype_instance_by_image_context_model['name'] = 'my-volume'
volume_prototype_instance_by_image_context_model[
    'profile'] = volume_profile_identity_model

image_identity_model = {}
image_identity_model['id'] = image_id

instance_profile_identity_model = {}
instance_profile_identity_model['name'] = profile

key_identity_model = {}
key_identity_model['id'] = key['id']

network_interface_prototype_model = {}

network_interface_prototype_model['subnet'] = subnet_identity_model

vpc_identity_model = {}
vpc_identity_model['id'] = vpc['id']

volume_attachment_prototype_instance_by_image = {}
volume_attachment_prototype_instance_by_image[
    'delete_volume_on_instance_delete'] = True
volume_attachment_prototype_instance_by_image[
    'name'] = 'my-volume-attachment'
volume_attachment_prototype_instance_by_image[
    'volume'] = volume_prototype_instance_by_image_context_model

zone_identity_model = {}
zone_identity_model['name'] = region+'-1'

instance_prototype_model = {}
instance_prototype_model['keys'] = [key_identity_model]
instance_prototype_model['name'] = 'my-instance'
instance_prototype_model['network_interfaces'] = [
    network_interface_prototype_model
]
instance_prototype_model['profile'] = instance_profile_identity_model

instance_prototype_model['user_data'] = userdata

instance_prototype_model['vpc'] = vpc_identity_model
instance_prototype_model[
    'boot_volume_attachment'] = volume_attachment_prototype_instance_by_image
instance_prototype_model['image'] = image_identity_model
instance_prototype_model[
    'primary_network_interface'] = network_interface_prototype_model
instance_prototype_model['zone'] = zone_identity_model

instance_prototype = instance_prototype_model

instance = vpcclient.create_instance(instance_prototype).get_result()
print('\nAll resources created()')

time.sleep(20)
# begin-delete_instance
print('\ndeleteVSI()')

response = vpcclient.delete_instance(id=instance['id'])
time.sleep(20)
print('\ndeleteKey()')

response = vpcclient.delete_key(id=key['id'])
print('\ndeleteSubnet()')

response = vpcclient.delete_subnet(id=subnet['id'])
time.sleep(20)
print('\ndeleteVPC()')

response = vpcclient.delete_vpc(id=vpc['id'])

print('\nAll created resources deleted()')
