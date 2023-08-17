package santander.cloud.sap.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import santander.cloud.sap.enums.PayloadTypeEnum;
import santander.cloud.sap.externalLib.SantanderOpenAPIConfig;
import santander.cloud.sap.models.DebitAccount;
import santander.cloud.sap.models.Workspace;
import santander.cloud.sap.models.WorkspaceResponse;
import santander.cloud.sap.repositories.WorkspaceRepository;
import santander.cloud.sap.service.DebitAccountService;
import santander.cloud.sap.service.WorkspaceService;

import java.util.*;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class WorkspaceServieImpl implements WorkspaceService {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceServieImpl.class);

    private final SantanderOpenAPIConfig santanderOpenAPIConfig;

    private final DebitAccountService debitAccountService;

    private final WorkspaceRepository workspaceRepository;

    @Override
    public WorkspaceResponse getWorkspaces(String workspaceId) {
        WorkspaceResponse workspaceResponse = new WorkspaceResponse();

        try {
            Map<PayloadTypeEnum, HashMap<String, String>> payloadMap = new HashMap<>();
            String url = santanderOpenAPIConfig.getWorkspacesGet(workspaceId);

            logger.info("Calling workspace api...");
            String result = santanderOpenAPIConfig.executeHttpRequest(payloadMap, RequestMethod.GET, url);
            logger.info("response string: {} ", result);
            workspaceResponse = isNull(workspaceId) ?
                    new Gson().fromJson(result, WorkspaceResponse.class)
                    : new WorkspaceResponse(Arrays.asList(new Gson().fromJson(result, Workspace.class)));
        } catch (Exception e) {
            logger.error("Error getting workspace: " + e.getMessage());
        }

        return workspaceResponse;
    }

    @Override
    public String getValidWorkspaceId(String workspaceId) {
        return getValidWorkspace(workspaceId).getId();
    }

    @Override
    public Workspace getValidWorkspace(String workspaceId) {
        if (isNull(workspaceId)) {
            Optional<Workspace> workspaceOptional = workspaceRepository.findFirstByOrderByIdDesc();

            workspaceId = workspaceOptional.isPresent() ? workspaceOptional.get().getId() : santanderOpenAPIConfig.getWorkspaceId();
        }

        WorkspaceResponse workspaceResponse = getWorkspaces(workspaceId);

        if(!isNull(workspaceResponse.getContent()) && isValid(workspaceResponse.getContent().get(0))){
            logger.warn("Workspace found is valid!");
            return workspaceResponse.getContent().get(0);
        }

        return createValidWorkspace();
    }

    private Workspace createValidWorkspace() {
        logger.warn("Workspace not found or invalid, creating a new one...");
        Workspace workspace = new Workspace();

        try {

            Map<PayloadTypeEnum, HashMap<String, String>> payloadMap = new HashMap<>();
            String url = santanderOpenAPIConfig.getApiWorkspaces();

            logger.info("Generating workspace post request...");
            HashMap<String, String> workspaceBody = getJsonBody();

            payloadMap.put(PayloadTypeEnum.JSON_BODY, workspaceBody);

            logger.info("Calling workspace post api...");
            String result = santanderOpenAPIConfig.executeHttpRequest(payloadMap, RequestMethod.POST, url);

            workspace = new Gson().fromJson(result, Workspace.class);

            List<DebitAccount> debitAccountList = new ArrayList<>();
            debitAccountList.add(workspace.getMainDebitAccount());
            debitAccountList.addAll(workspace.getAdditionalDebitAccounts());

            debitAccountService.setEntityIdList(debitAccountList);

            workspaceRepository.save(workspace);
        } catch (Exception e) {
            logger.error("Error posting workspace: " + e.getMessage());
        }

        return workspace;
    }

    private HashMap<String, String> getJsonBody() {
        HashMap<String, String> workspaceBody = new HashMap<>();
        workspaceBody.put("type", "\"PAYMENTS\"");
        workspaceBody.put("description", "\"Teste BTP\"");
        workspaceBody.put("pixPaymentsActive", "true");
        workspaceBody.put("barCodePaymentsActive", "true");
        workspaceBody.put("bankSlipPaymentsActive", "true");
        workspaceBody.put("bankSlipAvailableActive", "true");
        workspaceBody.put("mainDebitAccount", "{ \"branch\": \"" + santanderOpenAPIConfig.getMainDebitAccountBranch() +
                "\", \"number\": \"" + santanderOpenAPIConfig.getMainDebitAccountBranch() + "\" }");
        workspaceBody.put("additionalDebitAccounts", "[ { \"branch\": \"" + santanderOpenAPIConfig.getAdditionalDebitAccountBranch() +
                "\", \"number\": \"" + santanderOpenAPIConfig.getAdditionalDebitAccountBranch() + "\" } ]");

        return workspaceBody;
    }

    private boolean isValid(Workspace workspace) {
        return workspace.isBankSlipAvailableActive() &&
                workspace.isBankSlipPaymentsActive() &&
                workspace.isPixPaymentsActive() &&
                workspace.isBarCodePaymentsActive();
    }
}
