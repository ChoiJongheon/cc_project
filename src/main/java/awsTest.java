import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;

import java.util.*;

public class awsTest {
    /*
     * Cloud Computing, Data Computing Laboratory
     * Department of Computer Science
     * Chungbuk National University
     */
    static AmazonEC2 ec2;

    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-2") /* check the region at AWS console */
                .build();
    }



    public static void main(String[] args) throws Exception {
        init();
        Scanner menu = new Scanner(System.in);

        while (true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("                                                            ");
            System.out.println("  Cloud Computing, Computer Science Department              ");
            System.out.println("                   at Chungbuk National University   ?????????   ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones         ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("  9. instance monitoring         10. stop monitoring        ");
            System.out.println(" 11. list security group         12. create security group  ");
            System.out.println(" 13. delete security group       99. quit                   ");
            System.out.println("------------------------------------------------------------");
            System.out.print("Enter number: ");

            switch (menu.nextInt()) {
                case 1:
                    listInstances();
                    break;
                case 2:
                    availableZones();
                    break;
                case 3:
                    startInstances();
                    break;
                case 4:
                    availableRegions();
                    break;
                case 5:
                    stopInstances();
                    break;
                case 6:
                    createInstances();
                    break;
                case 7:
                    rebootInstances();
                    break;
                case 8:
                    listImages();
                    break;
                case 9:
                    monitorInstances();
                    break;
                case 10:
                    stopMonitorInstances();
                    break;
                case 11:
                    listSecurityGroup();
                    break;
                case 12:
                    createSecurityGroup();
                    break;
                case 13:
                    deleteSecurityGroup();
                    break;
                case 99:
                    return;
            }
        }
    }

    public static void listInstances()
    {
        System.out.println("Listing instances....");
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                                    "[AMI] %s, " +
                                    "[type] %s, " +
                                    "[state] %10s, " +
                                    "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }

    public static void availableZones(){

        DescribeAvailabilityZonesResult zones_response =
                ec2.describeAvailabilityZones();
        int zonecount = 0;

        System.out.println("availability zone list :  ");
        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf(
                    "[id] %s [zonename] %s  [region] %s \n",

                    zone.getZoneId(),
                    zone.getZoneName(),
                    zone.getRegionName()
            );
            zonecount++;
        }
        System.out.println("You can access " +zonecount+ " availability zones.");
    }

    public static void availableRegions(){
        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.printf(
                    "[region] %s " + "[endpoint] %s\n",
                    region.getRegionName(),
                    region.getEndpoint());
        }
    }

    public static void startInstances(){

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        System.out.print("Enter your Instance ID : ");

        Scanner id_string = new Scanner(System.in);
        String instanceId = id_string.nextLine();


        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instanceId);

        ec2.startInstances(request);

        InstanceStatus status = new InstanceStatus()
                .withInstanceId(instanceId);

        ec2.describeInstanceStatus();

        System.out.println(instanceId + " Instance is Started!");
    }

    public static void stopInstances(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        System.out.print("Enter your Instance ID : ");

        Scanner id_string = new Scanner(System.in);
        String instanceId = id_string.nextLine();

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instanceId);

        ec2.stopInstances(request);

        System.out.print( instanceId +" Instance is Stopped!");

    }

    public static void createInstances(){
        System.out.print("Enter your AMI_id : ");
        Scanner amiId = new Scanner(System.in);
        String ami_id = amiId.nextLine();
        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();


        System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                reservation_id, ami_id);
    }

    public static void rebootInstances() {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        System.out.print("Enter your Instance ID : ");

        Scanner id_string = new Scanner(System.in);
        String instanceId = id_string.nextLine();

        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instanceId);

        RebootInstancesResult response = ec2.rebootInstances(request);

        System.out.println(instanceId + " Instance is rebooted!");
    }

    public static void listImages() {
        System.out.println("Listing Images....");
            DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");
            DescribeImagesResult result = ec2.describeImages(request);

            for (Image image : result.getImages()) {
                System.out.printf(
                        "[id] %s, " +
                                "[Name] %s, " +
                                "[State] %s, " +
                                "[Owner] %10s ",
                        image.getImageId(),
                        image.getName(),
                        image.getState(),
                        image.getOwnerId());
            }
            System.out.println();
        }
    public static void monitorInstances(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        System.out.print("write your Instance ID : ");
        Scanner id_string = new Scanner(System.in);
        String instanceId = id_string.nextLine();

        MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instanceId);
        ec2.monitorInstances(request);
        System.out.printf("%s monitoring enabled ", instanceId);
    }
    public static void stopMonitorInstances(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        System.out.print("Enter your Instance ID : ");
        Scanner id_string = new Scanner(System.in);
        String instanceId = id_string.nextLine();

        UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instanceId);

        ec2.unmonitorInstances(request);
        System.out.printf("%s monitoring disabled ", instanceId);
    }

    public static void listSecurityGroup(){
        System.out.println("Listing Security Groups....");
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        DescribeSecurityGroupsResult result = ec2.describeSecurityGroups(request);

        for (SecurityGroup group : result.getSecurityGroups()) {
            System.out.printf(
                    "[id] %s, " +
                            "[Name] %s, " +
                            "[Description] %s, " +
                            "[Owner] %10s, \n",
                    group.getGroupId(),
                    group.getGroupName(),
                    group.getDescription(),
                    group.getOwnerId());
        }
        System.out.println();
    }

    public static void createSecurityGroup(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        System.out.print("Enter group name : ");
        Scanner groupName = new Scanner(System.in);
        String group_name = groupName.nextLine();

        System.out.print("Enter group description : ");
        Scanner groupDesc = new Scanner(System.in);
        String group_desc = groupDesc.nextLine();

        CreateSecurityGroupRequest create_request = new
                CreateSecurityGroupRequest()
                .withGroupName(group_name)
                .withDescription(group_desc);

        CreateSecurityGroupResult create_response =
                ec2.createSecurityGroup(create_request);

        System.out.printf("%s Security group is created! ", group_name);
    }

    public static void deleteSecurityGroup(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        System.out.print("Enter group id : ");
        Scanner groupId = new Scanner(System.in);
        String group_id = groupId.nextLine();

        DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest()
                .withGroupId(group_id);

        DeleteSecurityGroupResult response = ec2.deleteSecurityGroup(request);
        System.out.printf("%s Security group is deleted! ", group_id);
    }
}