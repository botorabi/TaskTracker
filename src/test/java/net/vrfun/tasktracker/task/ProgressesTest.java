/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.security.UserAuthenticator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class ProgressesTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private Tags tags;

    @Mock
    private UserAuthenticator userAuthenticator;

    private Progresses progresses;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        progresses = new Progresses(progressRepository,
                taskRepository,
                tagRepository,
                tags,
                userAuthenticator);

    }

    private void mockUserAsAdmin() {
        doReturn(true).when(userAuthenticator).isRoleAdmin();
    }

    private void mockUserAsTeamLead() {
        doReturn(true).when(userAuthenticator).isRoleTeamLead();
    }

    private void mockUser(@NonNull final String login, @NonNull final Long id) {
        doReturn(id).when(userAuthenticator).getUserId();
        doReturn(login).when(userAuthenticator).getUserLogin();
    }

    @Test
    public void getAllAsAdmin() {
        List<Progress> progs = new ArrayList<>();
        progs.add(new Progress());

        doReturn(progs).when(progressRepository).findAll();

        mockUserAsAdmin();

        assertThat(progresses.getAll().size()).isEqualTo(1);
    }

    @Test
    public void getAllAsTeamLead() {
        List<Progress> progs = new ArrayList<>();
        progs.add(new Progress());

        doReturn(progs).when(progressRepository).findAll();

        mockUserAsTeamLead();

        assertThat(progresses.getAll().size()).isEqualTo(1);
    }

    @Test
    public void getAllAsUser() {
        List<Progress> progs = new ArrayList<>();
        Progress progress = new Progress();
        progs.add(progress);

        doReturn(progs).when(progressRepository).findAll();

        assertThat(progresses.getAll().size()).isEqualTo(0);

        final Long userId = 42L;
        final String userLogin = "MyLogin";
        progress.setOwnerId(userId);
        progress.setOwnerName(userLogin);

        mockUser(userLogin, userId);
        doReturn(progs).when(progressRepository).findProgressByOwnerId(anyLong());

        assertThat(progresses.getAll().size()).isEqualTo(1);
        assertThat(progresses.getAll().get(0).getOwnerId()).isEqualTo(userId);
    }

    @Test
    public void getByIdAsAdmin() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional result = Optional.of(progress);

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUserAsAdmin();

        assertThat(progresses.get(42L)).isNotNull();
    }

    @Test
    public void getByIdAsTeamLead() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional result = Optional.of(progress);

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUserAsTeamLead();

        assertThat(progresses.get(42L)).isNotNull();
    }

    @Test
    public void getByIdAsOwner() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional result = Optional.of(progress);

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUser("MyLogin", 42L);

        assertThat(progresses.get(42L)).isNotNull();
    }

    @Test
    public void getByIdAsNonOwner() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional result = Optional.of(progress);

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUser("MyLogin", 45L);

        assertThat(progresses.get(42L)).isNull();
    }

    @Test
    public void getByIdNonExisting() {
        Optional result = Optional.empty();

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUser("MyLogin", 45L);

        assertThat(progresses.get(42L)).isNull();
    }

    @Test
    public void getTeamProgress() {
        List<Progress> progs = new ArrayList<>();
        Progress progress = new Progress();
        progs.add(progress);

        doReturn(progs).when(progressRepository).findProgressByTeam(anyLong());

        assertThat(progresses.getTeamProgress(anyLong()).size()).isEqualTo(1);
    }

    @Test
    public void createValidateInput() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();

        mockUser("MyLogin", 42L);
        mockUserAsAdmin();

        assertThatThrownBy(() -> progresses.create(reqProgressEdit)).as("Did not recognized invalid input").isInstanceOf(IllegalArgumentException.class);

        reqProgressEdit.setTask(45L);
        assertThatThrownBy(() -> progresses.create(reqProgressEdit)).as("Did not recognized invalid input").isInstanceOf(IllegalArgumentException.class);

        reqProgressEdit.setTitle("Title");
        assertThatThrownBy(() -> progresses.create(reqProgressEdit)).as("Did not recognized invalid input").isInstanceOf(IllegalArgumentException.class);

        reqProgressEdit.setReportYear(null);
        reqProgressEdit.setReportWeek(50);
        assertThatThrownBy(() -> progresses.create(reqProgressEdit)).as("Did not recognized invalid input").isInstanceOf(IllegalArgumentException.class);

        reqProgressEdit.setReportYear(2020);
        reqProgressEdit.setReportWeek(null);
        assertThatThrownBy(() -> progresses.create(reqProgressEdit)).as("Did not recognized invalid input").isInstanceOf(IllegalArgumentException.class);

        reqProgressEdit.setReportYear(2020);
        reqProgressEdit.setReportWeek(50);
        reqProgressEdit.setText("My Progress Text");

        doReturn(Optional.of(new Task())).when(taskRepository).findById(anyLong());

        reqProgressEdit.setTags(Arrays.asList("tag1", "tag2"));

        progresses.create(reqProgressEdit);

        verify(tags, times(2)).getOrCreate(anyString());

        verify(progressRepository, times(1)).save(any());
    }

    @Test
    public void setInvalidReportWeek() {
        Progress progress = new Progress();

        assertThatThrownBy(() -> progresses.setReportWeek(progress, Progresses.MAX_CALENDAR_WEEKS + 1, 0))
                .as("Did not recognized invalid report week!")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> progresses.setReportWeek(progress, 0, 0))
                .as("Did not recognized invalid report week!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setReportWeekTooFarInFuture() {
        Progress progress = new Progress();
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        currentWeek += Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1;
        if (currentWeek > Progresses.MAX_CALENDAR_WEEKS) {
            currentYear += 1;
            currentWeek -= Progresses.MAX_CALENDAR_WEEKS;
        }

        mockUser("MyLogin", 42L);

        final Integer setWeek = currentWeek;
        final Integer setYear = currentYear;

        assertThatThrownBy(() -> progresses.setReportWeek(progress, setWeek, setYear))
                .as("Did not detect unauthorized report week setting! Report week is too far in future!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setReportWeekTooFarInPast() {
        Progress progress = new Progress();
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        currentWeek -= (Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1);
        if (currentWeek <= 0) {
            currentYear -= 1;
            currentWeek += Progresses.MAX_CALENDAR_WEEKS;
        }

        mockUser("MyLogin", 42L);

        final Integer setWeek = currentWeek;
        final Integer setYear = currentYear;

        assertThatThrownBy(() -> progresses.setReportWeek(progress, setWeek, setYear))
                .as("Did not detect unauthorized report week setting! Report week is too far in the past!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setReportWeekAsAdmin() {
        Progress progress = new Progress();
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        currentWeek -= (Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1);
        if (currentWeek <= 0) {
            currentYear -= 1;
            currentWeek += Progresses.MAX_CALENDAR_WEEKS;
        }

        mockUserAsAdmin();
        mockUserAsTeamLead();

        final Integer setWeek = currentWeek;
        final Integer setYear = currentYear;

        progresses.setReportWeek(progress, setWeek, setYear);

        Calendar reportCalendar = progress.getReportWeek();

        assertThat(reportCalendar.get(Calendar.WEEK_OF_YEAR)).isEqualTo(setWeek);
        assertThat(reportCalendar.get(Calendar.YEAR)).isEqualTo(setYear);
    }

    @Test
    public void editProgressNonExisting() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(42L);

        doReturn(Optional.empty()).when(progressRepository).findById(anyLong());

        assertThatThrownBy(() -> progresses.editProgress(reqProgressEdit))
                .as("Did not detect invalid progress id!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void editProgressNonAuthorized() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(100L);

        Progress progress = new Progress("Owner", 45L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(anyLong());

        mockUser("MyLogin", 55L);

        assertThatThrownBy(() -> progresses.editProgress(reqProgressEdit))
                .as("Did not detect unauthorized progress access!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void editProgressPartial() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(100L);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(110L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(anyLong());

        mockUserAsAdmin();
        mockUserAsTeamLead();

        doAnswer((invocationOnMock) -> invocationOnMock.getArguments()[0])
                .when(progressRepository).save(any());

        Progress editedProgress = progresses.editProgress(reqProgressEdit);

        assertThat(editedProgress.getId()).isEqualTo(110L);
        assertThat(editedProgress.getTitle()).isNull();
        assertThat(editedProgress.getText()).isNull();
        assertThat(editedProgress.getTags()).isNull();

        reqProgressEdit.setReportWeek(12);
        reqProgressEdit.setReportYear(null);
        editedProgress = progresses.editProgress(reqProgressEdit);

        assertThat(editedProgress.getReportWeek()).isNull();

        reqProgressEdit.setReportWeek(null);
        reqProgressEdit.setReportYear(2020);
        editedProgress = progresses.editProgress(reqProgressEdit);

        assertThat(editedProgress.getReportWeek()).isNull();
    }

    @Test
    public void editProgressChangeTaskAsOwner() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(100L);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(110L);
        Task task = new Task("My Task");
        task.setId(200L);
        progress.setTask(task);

        Task taskNew = new Task("My New Task");
        taskNew.setId(300L);

        mockUser("Owner", 45L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(anyLong());
        doReturn(Optional.of(task)).when(taskRepository).findById(200L);
        doReturn(Optional.of(taskNew)).when(taskRepository).findById(300L);

        reqProgressEdit.setTask(300L);

        doAnswer((invocationOnMock) -> invocationOnMock.getArguments()[0])
                .when(progressRepository).save(any());

        Progress editedProgress = progresses.editProgress(reqProgressEdit);

        assertThat(editedProgress.getTask().getId()).isEqualTo(taskNew.getId());
        assertThat(editedProgress.getTask().getTitle()).isEqualTo(taskNew.getTitle());
    }

    @Test
    public void editProgressChangeToInvalidTaskAsTeamLead() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(100L);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(110L);
        Task task = new Task("My Task");
        task.setId(200L);
        progress.setTask(task);

        mockUserAsTeamLead();

        doReturn(Optional.of(progress)).when(progressRepository).findById(anyLong());
        doReturn(Optional.of(task)).when(taskRepository).findById(200L);
        doReturn(Optional.empty()).when(taskRepository).findById(300L);

        reqProgressEdit.setTask(300L);

        assertThatThrownBy(() -> progresses.editProgress(reqProgressEdit))
                .as("Did not detect invalid task ID!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void editProgress() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(100L);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(110L);
        Task task = new Task("My Task");
        task.setId(200L);
        progress.setTask(task);

        doReturn(Optional.of(progress)).when(progressRepository).findById(anyLong());
        doReturn(Optional.of(task)).when(taskRepository).findById(anyLong());

        mockUserAsAdmin();
        mockUserAsTeamLead();

        reqProgressEdit.setTitle("My Title");
        reqProgressEdit.setText("My Text");
        reqProgressEdit.setReportWeek(35);
        reqProgressEdit.setReportYear(2020);
        reqProgressEdit.setTags(Arrays.asList("tag1", "tag2"));
        reqProgressEdit.setTask(200L);

        doAnswer((invocationOnMock) -> invocationOnMock.getArguments()[0])
                .when(progressRepository).save(any());

        Progress editedProgress = progresses.editProgress(reqProgressEdit);

        assertThat(editedProgress.getId()).isEqualTo(110L);
        assertThat(editedProgress.getTitle()).isEqualTo("My Title");
        assertThat(editedProgress.getText()).isEqualTo("My Text");
        assertThat(editedProgress.getTags().size()).isEqualTo(2);
    }

    @Test
    public void updateProgressEntryTags() {
        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);
        List<String> tags = Arrays.asList("tagExisting", "tagNonExisting");

        doReturn(Optional.of(new Tag("tagExisting"))).when(tagRepository).findTagByName("tagExisting");
        doAnswer((invocationOnMock) -> invocationOnMock.getArguments()[0])
                .when(tagRepository).save(any());

        progresses.updateProgressEntryTags(progress, tags);

        assertThat(progress.getTags().size()).isEqualTo(2);
    }

    @Test
    public void deleteNonExisting() {
        assertThatThrownBy(() -> progresses.delete(0L))
                .as("Did not detect non-existing progress!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteAsAdmin() {
        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUserAsAdmin();

        progresses.delete(100L);

        verify(progressRepository, times(1)).delete(any());
    }

    @Test
    public void deleteAsTeamLead() {
        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUserAsTeamLead();

        progresses.delete(100L);

        verify(progressRepository, times(1)).delete(any());
    }

    @Test
    public void deleteAsAdminAndTeamLead() {
        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUserAsAdmin();
        mockUserAsTeamLead();

        progresses.delete(100L);

        verify(progressRepository, times(1)).delete(any());
    }

    @Test
    public void deleteUnauthorized() {
        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUser("UnAuthorizedUser", 42L);

        assertThatThrownBy(() -> progresses.delete(100L))
                .as("Did not detect unauthorized progress deletion!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteAuthorizedUser() {
        Calendar calendar = Calendar.getInstance();

        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);
        progress.setReportWeek(calendar);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUser("AuthorizedUser", 45L);

        progresses.delete(100L);

        verify(progressRepository, times(1)).delete(any());
    }


    @Test
    public void deleteAuthorizedUserInvalidWeekDistance() {
        Calendar calendar = Calendar.getInstance();
        int outOfRangeWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int outOfRangeYear = calendar.get(Calendar.YEAR);

        outOfRangeWeek -= (Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1);
        if (outOfRangeWeek <= 0) {
            outOfRangeYear -= 1;
            outOfRangeWeek += Progresses.MAX_CALENDAR_WEEKS;
        }

        calendar.set(Calendar.WEEK_OF_YEAR, outOfRangeWeek);
        calendar.set(Calendar.YEAR, outOfRangeYear);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);
        progress.setReportWeek(calendar);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUser("AuthorizedUser", 45L);

        assertThatThrownBy(() -> progresses.delete(100L))
                .as("Did not detect deleting a progress outside of allowed week range!")
                .isInstanceOf(IllegalArgumentException.class);
    }
}
