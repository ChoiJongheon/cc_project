import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

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
        Scanner id_string = new Scanner(System.in);

        int number = 3;
        while (true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("                                                            ");
            System.out.println("  Cloud Computing, Computer Science Department              ");
            System.out.println("                           at Chungbuk National University  ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones         ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");
            System.out.print("Enter an integer: ");

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

        Scanner amiId = new Scanner(System.in);
        String ami_id = amiId.nextLine();

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

        CreateTagsRequest tag_request = new CreateTagsRequest()
                .withResources(reservation_id);

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
                                "[Owner] %10s, ",
                        image.getImageId(),
                        image.getName(),
                        image.getState(),
                        image.getOwnerId());
            }
            System.out.println();
        }
}