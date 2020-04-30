/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.atlas.hook;

import org.apache.atlas.AtlasConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.kafka.clients.admin.AdminClient;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AtlasTopicCreatorTest {

    private final String ATLAS_HOOK_TOPIC     = AtlasConfiguration.NOTIFICATION_HOOK_TOPIC_NAME.getString();
    private final String ATLAS_ENTITIES_TOPIC = AtlasConfiguration.NOTIFICATION_ENTITIES_TOPIC_NAME.getString();

    @Test
    public void shouldNotCreateAtlasTopicIfNotConfiguredToDoSo() {

        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(false);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final boolean[] topicExistsCalled = new boolean[] {false};
        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {
            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                topicExistsCalled[0] = true;
                return false;
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC);
        assertFalse(topicExistsCalled[0]);
    }

    @Test
    public void shouldNotCreateTopicIfItAlreadyExists() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final AdminClient adminClient = mock(AdminClient.class);
        final boolean[] topicExistsCalled = new boolean[]{false};
        final boolean[] createTopicCalled = new boolean[]{false};

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {
            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                topicExistsCalled[0] = true;
                return true;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
                createTopicCalled[0] = true;
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC);
        assertTrue(topicExistsCalled[0]);
        assertFalse(createTopicCalled[0]);
    }

    @Test
    public void shouldCreateTopicIfItDoesNotExist() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final AdminClient adminClient = mock(AdminClient.class);

        final boolean[] createdTopic = new boolean[]{false};

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {
            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                return false;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
                createdTopic[0] = true;
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC);
        assertTrue(createdTopic[0]);
    }

    @Test
    public void shouldNotFailIfExceptionOccursDuringCreatingTopic() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final AdminClient adminClient = mock(AdminClient.class);
        final boolean[] createTopicCalled = new boolean[]{false};

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {
            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                return false;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
                createTopicCalled[0] = true;
                throw new RuntimeException("Simulating failure during creating topic");
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC);
        assertTrue(createTopicCalled[0]);
    }

    @Test
    public void shouldCreateMultipleTopics() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final AdminClient adminClient = mock(AdminClient.class);

        final Map<String, Boolean> createdTopics = new HashMap<>();
        createdTopics.put(ATLAS_HOOK_TOPIC, false);
        createdTopics.put(ATLAS_ENTITIES_TOPIC, false);

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {

            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                return false;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
                createdTopics.put(topicName, true);
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC, ATLAS_ENTITIES_TOPIC);
        assertTrue(createdTopics.get(ATLAS_HOOK_TOPIC));
        assertTrue(createdTopics.get(ATLAS_ENTITIES_TOPIC));
    }

    @Test
    public void shouldCreateTopicEvenIfEarlierOneFails() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final AdminClient adminClient = mock(AdminClient.class);

        final Map<String, Boolean> createdTopics = new HashMap<>();
        createdTopics.put(ATLAS_ENTITIES_TOPIC, false);

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {

            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                return false;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
                if (topicName.equals(ATLAS_HOOK_TOPIC)) {
                    throw new RuntimeException("Simulating failure when creating ATLAS_HOOK topic");
                } else {
                    createdTopics.put(topicName, true);
                }
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC, ATLAS_ENTITIES_TOPIC);
        assertTrue(createdTopics.get(ATLAS_ENTITIES_TOPIC));
    }

    @Test
    public void shouldCloseResources() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        when(configuration.getString("atlas.authentication.method.kerberos")).thenReturn("false");
        final AdminClient adminClient = mock(AdminClient.class);

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {
            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                return false;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC, ATLAS_ENTITIES_TOPIC);

        verify(adminClient, times(1)).close();
    }

    @Test
    public void shouldNotProcessTopicCreationIfSecurityFails() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getBoolean(AtlasTopicCreator.ATLAS_NOTIFICATION_CREATE_TOPICS_KEY, true)).
                thenReturn(true);
        final AdminClient adminClient = mock(AdminClient.class);
        final Map<String, Boolean> createdTopics = new HashMap<>();
        createdTopics.put(ATLAS_HOOK_TOPIC, false);
        createdTopics.put(ATLAS_ENTITIES_TOPIC, false);

        AtlasTopicCreator atlasTopicCreator = new AtlasTopicCreator() {
            @Override
            protected boolean ifTopicExists(String topicName, AdminClient adminClient) {
                return false;
            }

            @Override
            protected AdminClient createKafkaAdminClient(Configuration atlasProperties) {
                return adminClient;
            }

            @Override
            protected void createTopic(Configuration atlasProperties, String topicName, AdminClient adminClient) {
                createdTopics.put(topicName, true);
            }

            @Override
            protected boolean handleSecurity(Configuration atlasProperties) {
                return false;
            }
        };
        atlasTopicCreator.createAtlasTopic(configuration, ATLAS_HOOK_TOPIC, ATLAS_ENTITIES_TOPIC);
        assertFalse(createdTopics.get(ATLAS_HOOK_TOPIC));
        assertFalse(createdTopics.get(ATLAS_ENTITIES_TOPIC));
    }
}
