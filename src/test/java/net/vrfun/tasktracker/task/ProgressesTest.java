/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import net.vrfun.tasktracker.security.UserAuthenticator;
import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.TeamRepository;
import net.vrfun.tasktracker.user.User;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
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
    private TeamRepository teamRepository;

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

        progresses = new Progresses(
                progressRepository,
                taskRepository,
                teamRepository,
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

    private Task mockTaskBelongingToUser(long taskId) {
        Task userTask = new Task();
        userTask.setId(taskId);

        doReturn(Collections.singletonList(userTask)).when(taskRepository).findUserTasks(any());

        return userTask;
    }


    private Task mockTaskBelongingToTeam(long taskId) {
        Task task = new Task();
        task.setId(taskId);

        doReturn(Collections.singletonList(new Team())).when(teamRepository).findUserTeams(any());
        doReturn(Collections.singletonList(task)).when(taskRepository).findTeamTasks(any());

        return task;
    }

    @Test
    public void getAllAsAdmin() {
        List<Progress> progs = new ArrayList<>();
        progs.add(new Progress());

        doReturn(progs).when(progressRepository).findAllByOrderByReportWeekDesc(any());
        doReturn((long)progs.size()).when(progressRepository).count();

        mockUserAsAdmin();

        assertThat(progresses.getAll().size()).isEqualTo(1);
    }

    @Test
    public void getAllAsTeamLead() {
        List<Progress> progs = new ArrayList<>();
        progs.add(new Progress());

        doReturn(progs).when(progressRepository).findAllByOrderByReportWeekDesc(any());
        doReturn((long)progs.size()).when(progressRepository).count();

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
        doReturn(progs).when(progressRepository).findProgressByOwnerIdInOrderByReportWeekDesc(anyList(), any());
        doReturn((long)progs.size()).when(progressRepository).countProgressByOwnerIdIn(anyList());

        assertThat(progresses.getAll().size()).isEqualTo((long)progs.size());
        assertThat(progresses.getAll().get(0).getOwnerId()).isEqualTo(userId);
    }

    @Test
    public void getByIdAsAdmin() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional<Progress> result = Optional.of(progress);

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUserAsAdmin();

        assertThat(progresses.get(42L)).isNotNull();
    }

    @Test
    public void getByIdAsTeamLead() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional<Progress> result = Optional.of(progress);

        doReturn(result).when(progressRepository).findById(anyLong());

        mockUserAsTeamLead();

        assertThat(progresses.get(42L)).isNotNull();
    }

    @Test
    public void getByIdAsOwner() {
        Progress progress = new Progress();
        progress.setOwnerId(42L);

        Optional<Progress> result = Optional.of(progress);

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

        doReturn(progs).when(progressRepository).findByTaskId(anyLong());

        assertThat(progresses.getTeamProgress(anyLong()).size()).isEqualTo(1);
    }

    @Test
    public void createValidateInput() {
        final long TASK_ID = 45L;

        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();

        mockUserAsAdmin();
        mockTaskBelongingToUser(TASK_ID);

        assertThatThrownBy(() -> progresses.create(reqProgressEdit)).as("Did not recognized invalid input").isInstanceOf(IllegalArgumentException.class);

        reqProgressEdit.setTask(TASK_ID);
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

        doReturn(Optional.of(new Task())).when(taskRepository).findById(anyLong());

        reqProgressEdit.setTags(Arrays.asList("tag1", "tag2"));

        progresses.create(reqProgressEdit);

        verify(tags, times(2)).getOrCreate(anyString());

        verify(progressRepository, times(1)).save(any());
    }

    @Test
    public void createWithoutTags() {
        LocalDate date = LocalDate.now();
        int currentWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = date.get(IsoFields.WEEK_BASED_YEAR);

        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setTask(45L);
        reqProgressEdit.setTitle("My Title");
        reqProgressEdit.setText("My Progress Text");
        reqProgressEdit.setReportYear(currentYear);
        reqProgressEdit.setReportWeek(currentWeek);

        Task userTask = new Task();
        userTask.setId(reqProgressEdit.getTask());

        mockUser("MyLogin", 42L);

        doReturn(Collections.singletonList(userTask)).when(taskRepository).findUserTasks(any());
        doReturn(Optional.of(userTask)).when(taskRepository).findById(anyLong());
        doAnswer((invocationOnMock) -> invocationOnMock.getArgument(0))
                .when(progressRepository).save(any());

        Progress progress = progresses.create(reqProgressEdit);

        LocalDate reportWeek = progress.getReportWeek();

        assertThat(progress.getTitle()).isEqualTo("My Title");
        assertThat(progress.getText()).isEqualTo("My Progress Text");
        assertThat(reportWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)).isEqualTo(currentWeek);
        assertThat(reportWeek.get(IsoFields.WEEK_BASED_YEAR)).isEqualTo(currentYear);
    }

    @Test
    public void checkTaskBelongsToUserUserTask() {
        mockTaskBelongingToUser(45L);

        assertThat(progresses.checkTaskBelongsToUser(new User(), 45L)).isTrue();
        assertThat(progresses.checkTaskBelongsToUser(new User(), 100L)).isFalse();
    }

    @Test
    public void checkTaskBelongsToUserTeamTask() {
        mockTaskBelongingToTeam(45L);

        assertThat(progresses.checkTaskBelongsToUser(new User(), 45L)).isTrue();
        assertThat(progresses.checkTaskBelongsToUser(new User(), 100L)).isFalse();
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
    public void checkWeekDistance() {
        int currentYear = 2020;
        int currentWeek = 1;
        LocalDate date = LocalDate.of(currentYear, 1, currentWeek);

        assertThat(progresses.checkWeekDistance(date, currentWeek, currentYear)).isTrue();
        assertThat(progresses.checkWeekDistance(date, currentWeek, currentYear + 2)).isFalse();

        // future date, same year
        assertThat(progresses.checkWeekDistance(date, currentWeek + Progresses.MAX_CALENDAR_WEEK_DISTANCE, currentYear)).isTrue();
        assertThat(progresses.checkWeekDistance(date, currentWeek + Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1, currentYear)).isFalse();

        // past date, previous year
        assertThat(progresses.checkWeekDistance(date, currentWeek - Progresses.MAX_CALENDAR_WEEK_DISTANCE + Progresses.MAX_CALENDAR_WEEKS, currentYear - 1)).isTrue();
        assertThat(progresses.checkWeekDistance(date, currentWeek - Progresses.MAX_CALENDAR_WEEK_DISTANCE - 1 + Progresses.MAX_CALENDAR_WEEKS, currentYear - 1)).isFalse();

        // future date, next year
        date = LocalDate.of(currentYear, 12, 29);

        assertThat(progresses.checkWeekDistance(date, Progresses.MAX_CALENDAR_WEEK_DISTANCE, currentYear + 1)).isTrue();
        assertThat(progresses.checkWeekDistance(date, Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1, currentYear + 1)).isFalse();
    }

    @Test
    public void setReportWeekTooFarInFuture() {
        Progress progress = new Progress();

        LocalDate date = LocalDate.now();
        int currentWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = date.get(IsoFields.WEEK_BASED_YEAR);

        currentWeek += Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1;
        if (currentWeek > Progresses.MAX_CALENDAR_WEEKS) {
            currentYear += 1;
            currentWeek -= Progresses.MAX_CALENDAR_WEEKS;
        }

        mockUser("MyLogin", 42L);

        final int setWeek = currentWeek;
        final int setYear = currentYear;

        assertThatThrownBy(() -> progresses.setReportWeek(progress, setWeek, setYear))
                .as("Did not detect unauthorized report week setting! Report week is too far in future!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setReportWeekTooFarInPast() {
        Progress progress = new Progress();

        LocalDate date = LocalDate.now();
        int currentWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = date.get(IsoFields.WEEK_BASED_YEAR);

        currentWeek -= (Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1);
        if (currentWeek <= 0) {
            currentYear -= 1;
            currentWeek += Progresses.MAX_CALENDAR_WEEKS;
        }

        mockUser("MyLogin", 42L);

        final int setWeek = currentWeek;
        final int setYear = currentYear;

        assertThatThrownBy(() -> progresses.setReportWeek(progress, setWeek, setYear))
                .as("Did not detect unauthorized report week setting! Report week is too far in the past!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setReportWeekAsAdmin() {
        Progress progress = new Progress();

        LocalDate date = LocalDate.now();
        int currentWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = date.get(IsoFields.WEEK_BASED_YEAR);

        currentWeek -= (Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1);
        if (currentWeek <= 0) {
            currentYear -= 1;
            currentWeek += Progresses.MAX_CALENDAR_WEEKS;
        }

        mockUserAsAdmin();
        mockUserAsTeamLead();

        final int setWeek = currentWeek;
        final int setYear = currentYear;

        progresses.setReportWeek(progress, setWeek, setYear);

        LocalDate reportWeek = progress.getReportWeek();

        assertThat(reportWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)).isEqualTo(setWeek);
        assertThat(reportWeek.get(IsoFields.WEEK_BASED_YEAR)).isEqualTo(setYear);
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
    public void editProgressChangeToSameTaskAsOwner() {
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setId(100L);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(110L);
        Task task = new Task("My Task");
        task.setId(200L);
        progress.setTask(task);

        mockUser("Owner", 45L);

        doReturn(Optional.of(progress)).when(progressRepository).findById(anyLong());
        doReturn(Optional.of(task)).when(taskRepository).findById(200L);

        reqProgressEdit.setTask(200L);

        doAnswer((invocationOnMock) -> invocationOnMock.getArguments()[0])
                .when(progressRepository).save(any());

        Progress editedProgress = progresses.editProgress(reqProgressEdit);

        assertThat(editedProgress.getTask().getId()).isEqualTo(task.getId());
        assertThat(editedProgress.getTask().getTitle()).isEqualTo(task.getTitle());
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
    public void setProgressTaskAndTagsNonExistingTask() {
        Progress progress = new Progress();
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setTask(42L);

        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        assertThatThrownBy(() -> progresses.setProgressTaskAndTags(progress, reqProgressEdit))
                .as("Did not detect non-existing task!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setProgressTaskAndTagsWithTags() {
        Progress progress = new Progress();
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setTask(42L);
        reqProgressEdit.setTags(Arrays.asList("tag1", "tag2"));

        doReturn(Optional.of(new Task("My Task"))).when(taskRepository).findById(anyLong());
        doAnswer((invocationOnMock) -> new Tag(invocationOnMock.getArgument(0)))
                .when(tags).getOrCreate(any());

        progresses.setProgressTaskAndTags(progress, reqProgressEdit);

        assertThat(progress.getTask().getTitle()).isEqualTo("My Task");

        List<Tag> taskTags = new ArrayList<>(progress.getTags());

        assertThat(taskTags.size()).isEqualTo(2);
        assertThat(taskTags.get(0).getName()).isEqualTo("tag1");
        assertThat(taskTags.get(1).getName()).isEqualTo("tag2");
    }

    @Test
    public void setProgressTaskAndTagsWithoutTags() {
        Progress progress = new Progress();
        ReqProgressEdit reqProgressEdit = new ReqProgressEdit();
        reqProgressEdit.setTask(42L);

        doReturn(Optional.of(new Task("My Task"))).when(taskRepository).findById(anyLong());

        progresses.setProgressTaskAndTags(progress, reqProgressEdit);

        assertThat(progress.getTags()).isEmpty();
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
        LocalDate date = LocalDate.now();

        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);
        progress.setReportWeek(date);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUser("AuthorizedUser", 45L);

        progresses.delete(100L);

        verify(progressRepository, times(1)).delete(any());
    }

    @Test
    public void deleteAuthorizedUserInvalidWeekDistance() {
        LocalDate date = LocalDate.now();

        date = date.minusWeeks(Progresses.MAX_CALENDAR_WEEK_DISTANCE + 1);

        Progress progress = new Progress("Owner", 45L);
        progress.setId(100L);
        progress.setReportWeek(date);

        doReturn(Optional.of(progress)).when(progressRepository).findById(100L);

        mockUser("AuthorizedUser", 45L);

        assertThatThrownBy(() -> progresses.delete(100L))
                .as("Did not detect deleting a progress outside of allowed week range!")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getPagedAsAdmin() {
        mockUserAsAdmin();
        List<Progress> progs = new ArrayList<>();
        progs.add(new Progress());

        doReturn(progs).when(progressRepository).findAllByOrderByReportWeekDesc(any());
        doReturn(Long.valueOf(progs.size())).when(progressRepository).count();

        ProgressPagedDTO progressPagedDTO = progresses.getPaged(0, 10);

        assertThat(progressPagedDTO.getTotalCount()).isEqualTo(1L);
        assertThat(progressPagedDTO.getProgressList()).hasSize(1);

    }

    @Test
    public void getPagedAsTeamLead() {
        mockUserAsTeamLead();

        ProgressPagedDTO progressPagedDTO = progresses.getPaged(0, 10);

        assertThat(progressPagedDTO.getTotalCount()).isEqualTo(0L);
    }

    @Test
    public void getPagedEmpty() {
        ProgressPagedDTO progressPagedDTO = progresses.getPaged(0, 10);

        assertThat(progressPagedDTO.getTotalCount()).isEqualTo(0L);
    }

    @Test
    public void getPagedNoEmpty() {
        final long EXPECTED_ENTRIES = 4L;
        List<Progress> progs = new ArrayList<>();
        for (long i = 0; i < EXPECTED_ENTRIES; i++) {
            progs.add(new Progress());
        }
        doReturn(EXPECTED_ENTRIES).when(progressRepository).countProgressByOwnerIdIn(anyList());
        doReturn(progs).when(progressRepository).findProgressByOwnerIdInOrderByReportWeekDesc(anyList(), any());

        ProgressPagedDTO progressPagedDTO = progresses.getPaged(0, 10);

        assertThat(progressPagedDTO.getTotalCount()).isEqualTo(EXPECTED_ENTRIES);
    }

    @Test
    public void findAllTeamLeadRelatedUsers() {
        User teamLead = new User();
        teamLead.setId(42L);
        List<Team> teams = new ArrayList<>();

        doReturn(teams).when(teamRepository).findUserTeams(any());

        assertThat(progresses.findAllTeamLeadRelatedUsers(teamLead)).isEmpty();

        Team team = new Team("team", "great team");
        team.setId(100L);
        team.setTeamLeaders(Collections.singletonList(teamLead));
        team.setUsers(Collections.singletonList(teamLead));
        teams.add(team);

        assertThat(progresses.findAllTeamLeadRelatedUsers(teamLead)).hasSize(2);
    }
}
