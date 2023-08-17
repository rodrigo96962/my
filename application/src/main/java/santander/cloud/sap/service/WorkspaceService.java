package santander.cloud.sap.service;

import santander.cloud.sap.models.Workspace;
import santander.cloud.sap.models.WorkspaceResponse;

public interface WorkspaceService {

    WorkspaceResponse getWorkspaces(String workspaceId);

    String getValidWorkspaceId(String workspaceId);

    Workspace getValidWorkspace(String workspaceId);
}
