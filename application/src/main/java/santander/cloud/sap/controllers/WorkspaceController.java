package santander.cloud.sap.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import santander.cloud.sap.models.WorkspaceResponse;
import santander.cloud.sap.service.WorkspaceService;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
@CrossOrigin
/*@CrossOrigin(origins = {
        "https://hubDePocs-quiet-kob-rw.cfapps.us10-001.hana.ondemand.com/",
        "http://localhost:5500",
        "http://localhost:4200"
})*/
public class WorkspaceController
{
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceController.class);

    private final WorkspaceService workspaceService;

    @GetMapping("/all")
    public ResponseEntity<WorkspaceResponse> getWorkspaces(){
        return ResponseEntity.ok(workspaceService.getWorkspaces(null));
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspaces(@PathVariable("workspaceId") String workspaceId){
        return ResponseEntity.ok(workspaceService.getWorkspaces(workspaceId));
    }

    @GetMapping("/validWorkspace")
    public ResponseEntity<WorkspaceResponse> getValidWorkspace(){
        return ResponseEntity.ok(new WorkspaceResponse(Arrays.asList(workspaceService.getValidWorkspace(null))));
    }
}
