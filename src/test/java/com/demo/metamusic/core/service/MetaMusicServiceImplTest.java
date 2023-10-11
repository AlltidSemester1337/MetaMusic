package com.demo.metamusic.core.service;

class MetaMusicServiceImplTest {

    /*@Test
    void registerExpectedBrokerInformation() {
        Map<String, metamusicInformation> testMap = new HashMap<>();
        metamusicServiceImpl brokerService = new metamusicServiceImpl(testMap);
        brokerService.registerBroker(TestConstants.BROKER_NAME, TestConstants.BROKER_INFORMATION);
        assertEquals(1, testMap.size());
        assertEquals(TestConstants.BROKER_INFORMATION, testMap.get(TestConstants.BROKER_NAME));
    }

    @Test
    void givenValidBrokerName_whenGetBrokerInformation_returnsExpectedBrokerInformation() {
        Map<String, metamusicInformation> testMap = new HashMap<>();
        testMap.put(TestConstants.BROKER_NAME, TestConstants.BROKER_INFORMATION);
        metamusicServiceImpl brokerService = new metamusicServiceImpl(testMap);
        assertEquals(TestConstants.BROKER_INFORMATION, brokerService.getBrokerInformation(TestConstants.BROKER_NAME));
    }

    @Test
    void givenValidBrokerName_whenDeleteBrokerInformation_returnsExpectedResult() {
        Map<String, metamusicInformation> testMap = new HashMap<>();
        String anotherBrokerName = TestConstants.BROKER_NAME + "2";

        testMap.put(TestConstants.BROKER_NAME, TestConstants.BROKER_INFORMATION);
        testMap.put(anotherBrokerName, TestConstants.BROKER_INFORMATION);
        metamusicServiceImpl brokerService = new metamusicServiceImpl(testMap);

        assertEquals(2, testMap.size());
        brokerService.deleteBrokerInformation(TestConstants.BROKER_NAME);

        assertEquals(1, testMap.size());
        assertEquals(TestConstants.BROKER_INFORMATION, brokerService.getBrokerInformation(anotherBrokerName));
    }

     */

}