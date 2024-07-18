package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;

@ZeebeProcessTest
class ZeebeWorkerManagerTest {

        ZeebeClient zeebeClient;
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        static final String JOB_ID = "test-skill";

        @Test
        void testDeployWorker() throws InterruptedException {

                ZeebeWorkerManager workerManager = new ZeebeWorkerManager(zeebeClient,
                                executorService);

                TestListener testListener = Mockito.spy(new TestListener());

                JobWorker worker = workerManager.deployWorker(buildTestSkill(JOB_ID, testListener));

                assertThat(worker).isEqualTo(workerManager.workers.peek());

                deployAndInstantiateTestBPMN();

                Thread.sleep(1000);

                verify(testListener, times(1)).call(any());
        }

        @Test
        void testAbortWorker() throws InterruptedException {
                ZeebeWorkerManager workerManager = new ZeebeWorkerManager(zeebeClient, executorService);

                TestListener testListener = Mockito.spy(new TestListener());

                JobWorker worker = workerManager.deployWorker(buildTestSkill(JOB_ID, testListener));

                assertThat(worker).isEqualTo(workerManager.workers.peek());

                deployAndInstantiateTestBPMN();

                workerManager.abortWorker(worker);

                assertThat(workerManager.workers).doesNotContain(worker);

                deployAndInstantiateTestBPMN(); // shouldn't add +1 invocation

                Thread.sleep(1000);

                verify(testListener, times(1)).call(any());
        }

        void deployAndInstantiateTestBPMN() {
                zeebeClient.newDeployResourceCommand().addResourceFromClasspath("test-skill.bpmn").send()
                                .thenCompose(p -> zeebeClient.newCreateInstanceCommand()
                                                .bpmnProcessId(p.getProcesses().get(0).getBpmnProcessId())
                                                .latestVersion().send())
                                .toCompletableFuture().join();
        }

        static Skill buildTestSkill(String skillId, TestListener listener) {
                return new Skill(skillId, listener::call);
        }

        class TestListener {
                public <T> T call(T in) {
                        return in;
                }
        }
}
