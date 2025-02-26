package bootstrappers.bootstraping;

import core.application.controllers.ApplicationRegisterController;
import core.application.controllers.UploadRequirementsAnswersController;
import core.domain.application.Application;
import core.domain.application.Status;
import core.domain.candidate.Candidate;
import core.domain.jobOpening.JobOpening;
import core.domain.user.Jobs4URoles;
import core.persistence.PersistenceContext;
import core.repositories.ApplicationRepository;
import core.repositories.CandidateRepository;
import core.repositories.JobOpeningRepository;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import eapli.framework.infrastructure.authz.domain.repositories.UserRepository;
import eapli.framework.actions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Bootstraps applications data.
 * This bootstrapper initializes sample applications, assigns ranks, and uploads requirements responses.
 * It also changes the status of the applications for testing purposes.
 * The applications are associated with job openings and candidates.
 * Requires a registered operator user to execute.
 * This class is an Action to be used in the bootstrapping process.
 *
 * @author Tomás Gonçalves
 */
public class ApplicationsBootstrapper implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationsBootstrapper.class);

    private final ApplicationRegisterController controller = new ApplicationRegisterController();
    private final JobOpeningRepository jobOpeningRepository = PersistenceContext.repositories().jobOpenings();
    private final CandidateRepository candidateRepository = PersistenceContext.repositories().candidates();
    private final UserRepository userRepository = PersistenceContext.repositories().users();
    private final ApplicationRepository applicationRepository = PersistenceContext.repositories().applications();
    private final UploadRequirementsAnswersController uploadRequirementsAnswersController = new UploadRequirementsAnswersController();

    /**
     * Executes the bootstrapping process for applications data.
     * Initializes sample applications, assigns ranks, uploads requirements responses, and changes application statuses.
     * @return true if bootstrapping is successful, false otherwise
     */
    @Override
    public boolean execute() {
        List<JobOpening> jobOpenings = (List<JobOpening>) jobOpeningRepository.allJobOpenings();
        List<Candidate> candidates = (List<Candidate>) candidateRepository.allCandidates();
        List<SystemUser> users = (List<SystemUser>) userRepository.findAll();

        List<SystemUser> operators = new ArrayList<>();
        for (SystemUser user : users) {
            if (user.hasAny(Jobs4URoles.OPERATOR)) {
                operators.add(user);
            }
        }
        SystemUser operator = operators.get(0);

        registerApplication("Not Ranked", "App1", jobOpenings.get(0), candidates.get(0), operator);
        registerApplication("1", "App2", jobOpenings.get(0), candidates.get(1), operator);
        registerApplication("4", "App3", jobOpenings.get(0), candidates.get(3), operator);
        registerApplication("Not Ranked", "App4", jobOpenings.get(0), candidates.get(4), operator);
        registerApplication("1", "App5", jobOpenings.get(1), candidates.get(0), operator);
        registerApplication("Not Ranked", "App6", jobOpenings.get(1), candidates.get(1), operator);
        registerApplication("Not Ranked", "App7", jobOpenings.get(1), candidates.get(2), operator);
        registerApplication("2", "App8", jobOpenings.get(1), candidates.get(3), operator);
        registerApplication("Not Ranked", "App9", jobOpenings.get(1), candidates.get(4), operator);
        registerApplication("Not Ranked", "App10", jobOpenings.get(2), candidates.get(0), operator);
        registerApplication("2", "App11", jobOpenings.get(2), candidates.get(1), operator);

        List<Application> applications = (List<Application>) applicationRepository.allApplications();

        insertRequirementsResponses("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt", applications.get(0));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements1.txt"), applications.get(1));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements1.txt"), applications.get(2));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt"), applications.get(3));

        insertRequirementsResponses("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt", applications.get(4));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements1.txt"), applications.get(5));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements1.txt"), applications.get(6));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt"), applications.get(7));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt"), applications.get(8));

        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt"), applications.get(9));
        insertRequirementsResponses(("jobs4u.core/src/main/resources/answered/requirements/requirements2.txt"), applications.get(10));


        changeApplicationStatus(applications.get(0), Status.DECLINED);
        changeApplicationStatus(applications.get(1), Status.ACCEPTED);
        changeApplicationStatus(applications.get(2), Status.ACCEPTED);
        changeApplicationStatus(applications.get(3), Status.DECLINED);
        changeApplicationStatus(applications.get(4), Status.RECEIVED);
        changeApplicationStatus(applications.get(5), Status.RECEIVED);
        changeApplicationStatus(applications.get(6), Status.RECEIVED);
        changeApplicationStatus(applications.get(7), Status.RECEIVED);
        changeApplicationStatus(applications.get(8), Status.RECEIVED);
        changeApplicationStatus(applications.get(9), Status.RECEIVED);
        changeApplicationStatus(applications.get(10), Status.ACCEPTED);

        return true;
    }

    /**
     * Registers an application with the given rank, application files, job reference, candidate, and operator.
     * @param rank the rank of the application
     * @param applicationFiles the application files
     * @param jobReference the job opening reference
     * @param candidate the candidate applying
     * @param operator the operator registering the application
     */
    private void registerApplication(String rank, String applicationFiles, JobOpening jobReference, Candidate candidate, SystemUser operator) {
        controller.registerApplication(rank, applicationFiles, jobReference, candidate, operator);
        LOGGER.debug("»»» Registering application {}", applicationFiles);
    }

    /**
     * Inserts the requirements responses for the given candidate requirements path and application.
     * @param candidateRequirementsPath the path to the candidate requirements file
     * @param application the application to insert the requirements responses
     */
    private void insertRequirementsResponses(String candidateRequirementsPath, Application application) {
        List<String> requirements = uploadRequirementsAnswersController.retrieveResponseRequirements(candidateRequirementsPath);
        uploadRequirementsAnswersController.uploadRequirements(requirements, application);
        LOGGER.debug("»»» Changing requirements responses {}", candidateRequirementsPath);
    }

    /**
     * Changes the status of the given application to the given status.
     * @param application the application to change the status
     * @param status the status to change the application to
     */
    private void changeApplicationStatus(Application application, Status status) {
        application.changeStatus(status);
        applicationRepository.save(application);
        LOGGER.debug("»»» Changing application status to {}", status);
    }

}