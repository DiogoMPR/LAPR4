package backoffice.presentation.jobRequirementsSpecifications;

import console.presentation.utils.ConsoleColors;
import core.application.controllers.*;
import core.domain.application.Application;
import core.domain.application.CandidateRequirements;
import core.domain.application.Status;
import core.domain.jobOpening.JobOpening;
import core.domain.jobOpening.JobReference;
import eapli.framework.presentation.console.AbstractUI;

import java.util.Map;

/**
 * The VerificationRequirementsUI class is responsible for displaying the
 * user interface related to the verification of job requirements. It interacts
 * with the VerificationRequirementsController to fetch job openings, retrieve
 * applications, and verify candidate requirements.
 *
 * @author Tomás Gonçalves
 */
public class VerificationRequirementsUI extends AbstractUI {

    private final VerificationRequirementsController verificationRequirementsController = new VerificationRequirementsController();

    /**
     * Displays the UI and handles the logic for verifying job requirements.
     *
     * @return true if the UI should be displayed again, false otherwise.
     */
    @Override
    protected boolean doShow() {
        JobOpening jobOpening = verificationRequirementsController.selectJobOpening();
        JobReference jobReference = jobOpening.jobReference();

        Iterable<Application> jobOpeningApplications = verificationRequirementsController.allApplicationsOfJobOpeningReceived(jobReference);

        if (jobOpeningApplications == null) {
            System.out.println(ConsoleColors.RED + "No applications for this job opening" + ConsoleColors.RESET);
        } else {
            for (Application applicationToVerify : jobOpeningApplications) {
                CandidateRequirements candidateRequirements = applicationToVerify.candidateRequirements();
                String path = jobOpening.jobRequirementsSpecification().jobRequirementsPath();

                if (candidateRequirements.toString() != null) {
                    Map<String, String> clientRequirements = verificationRequirementsController.mapCandidate(candidateRequirements.candidateRequirements());

                    boolean result = verificationRequirementsController.pluginRequirements(path, clientRequirements);

                    Status statusFinal;

                    if (result) {
                        statusFinal = Status.ACCEPTED;
                        verificationRequirementsController.changeJobInterviewStatus(statusFinal, applicationToVerify);
                        System.out.println(ConsoleColors.GREEN + "The candidate is valid for this job opening" + ConsoleColors.RESET);
                    } else {
                        statusFinal = Status.DECLINED;
                        verificationRequirementsController.changeJobInterviewStatus(statusFinal, applicationToVerify);
                        System.out.println(ConsoleColors.RED + "This candidate isn't valid for this job opening" + ConsoleColors.RESET);
                    }

                    System.out.println("=========================================================================================================");
                }
            }
        }

        return true;
    }

    /**
     * Returns the headline for the UI.
     *
     * @return A description as the headline.
     */
    @Override
    public String headline() {
        return "Verification of Requirements";
    }

}
