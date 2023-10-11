package com.demo.metamusic.adapter.persistence;


import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
class MetaMusicRepositoryImplIT {

    /*@Container
    HiveMQContainer container = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"));

    private Mqtt5BlockingClient testClient;
    private ScheduledExecutorService scheduledExecutorService;

    @BeforeEach
    void setUp() {
        testClient = Mqtt5Client.builder()
                .serverPort(container.getMqttPort())
                .buildBlocking();
        testClient.connect();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @AfterEach
    void tearDown() {
        testClient.disconnect();
    }

    @Test
    void givenValidBrokerInfoTopicAndMessage_shouldBroadcast() throws InterruptedException {
        MQTTServiceImpl mqttService = new MQTTServiceImpl("", "");
        String testMsg = "testMsg";
        String testTopic = "testTopic";

        testClient.subscribeWith()
                .topicFilter(testTopic)
                .qos(MqttQos.EXACTLY_ONCE)
                .send();

        Mqtt5BlockingClient.Mqtt5Publishes publishedMessages = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        mqttService.broadCastMessage(new metamusicInformation(container.getHost(), container.getMqttPort()), testTopic, testMsg);

        Mqtt5Publish receivedMessage = publishedMessages.receive();
        final String payload = new String(receivedMessage.getPayloadAsBytes(), UTF_8);
        assertEquals(testMsg, payload);
    }

    @Test
    void givenValidBrokerInfoTopicAndMessageSent_shouldReturnExpectedMessage() throws InterruptedException {
        MQTTServiceImpl mqttService = new MQTTServiceImpl("", "");
        String testMsg = "testMsg";
        String testTopic = "testTopic";

        scheduledExecutorService.schedule(() -> testClient.publishWith()
                .topic(testTopic)
                .payload(UTF_8.encode(testMsg))
                .send(), 200, TimeUnit.MILLISECONDS);

        String receivedMessage = mqttService.awaitNextMessage(new metamusicInformation(container.getHost(), container.getMqttPort()), testTopic);

        assertEquals(testMsg, receivedMessage);
    }

     */
}