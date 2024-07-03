package org.eclipse.digitaltwin.basyx.arena.workermanager.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.DispatchedSkill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.Skill;
import org.eclipse.digitaltwin.basyx.arena.workermanager.skills.SynchronizeSkillsResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;

@ZeebeProcessTest
class ZeebeSkillWorkerDispatcherTest {
    static final String BPMN_TEST_RESOURCE = "skill-dispatcher.bpmn";

    ZeebeClient zeebeClient;

    @Test
    void testSynchronizeSkills() throws InterruptedException {
        ZeebeSkillWorkerDispatcher dispatcher = new ZeebeSkillWorkerDispatcher(new ZeebeWorkerManager(zeebeClient));

        List<TestListener> oldListeners = List.of(new TestListener(), new TestListener(), new TestListener()).stream()
                .map(Mockito::spy)
                .toList();

        List<Skill> oldSkills = buildIterativeSkills(oldListeners);

        SynchronizeSkillsResult result = dispatcher.synchronizeSkills(oldSkills);

        assertThat(result.abortedSkills()).isEmpty();
        assertThat(result.failedToDispatchAny()).isFalse();
        assertThat(extractSkills(result.dispatchedSkillWorkers())).containsExactlyInAnyOrderElementsOf(oldSkills);

        List<TestListener> newListeners = List.of(new TestListener(), new TestListener()).stream()
                .map(Mockito::spy)
                .toList();

        List<Skill> newSkills = buildIterativeSkills(newListeners);

        result = dispatcher.synchronizeSkills(newSkills);

        assertThat(result.failedToDispatchAny()).isFalse();
        assertThat(extractSkills(result.abortedSkills()))
                .containsExactlyInAnyOrderElementsOf(oldSkills.subList(0, 2));
        assertThat(extractSkills(result.dispatchedSkillWorkers())).containsExactlyInAnyOrderElementsOf(newSkills);

        deployAndInstantiateTestBPMN();

        Thread.sleep(1000);

        // All newListeners should be called, but only oldListener #2 should be called.
        verify(newListeners.get(0), times(1)).call(any());
        verify(newListeners.get(1), times(1)).call(any());
        verify(oldListeners.get(0), times(0)).call(any());
        verify(oldListeners.get(1), times(0)).call(any());
        verify(oldListeners.get(2), times(1)).call(any());
    }

    void deployAndInstantiateTestBPMN() {
        zeebeClient.newDeployResourceCommand().addResourceFromClasspath(BPMN_TEST_RESOURCE).send()
                .thenCompose(p -> zeebeClient.newCreateInstanceCommand()
                        .bpmnProcessId(p.getProcesses().get(0).getBpmnProcessId())
                        .latestVersion().send())
                .toCompletableFuture().join();
    }

    static Iterable<Skill> extractSkills(
            Iterable<DispatchedSkill> dispatchedSkillWorkers) {
        return StreamSupport.stream(dispatchedSkillWorkers.spliterator(), false)
                .map(DispatchedSkill::getSkill)
                .toList();
    }

    static List<Skill> buildIterativeSkills(List<TestListener> listeners) {
        String prefix = "skill";
        int skillNum = 0;
        List<Skill> skills = new ArrayList<>();

        for (TestListener listener : listeners) {
            skills.add(buildTestSkill(prefix + skillNum, listener));
            skillNum++;
        }

        return skills;
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
