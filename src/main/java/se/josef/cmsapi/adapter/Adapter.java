package se.josef.cmsapi.adapter;

import lombok.extern.slf4j.Slf4j;
import se.josef.cmsapi.service.ProjectService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Slf4j
public abstract class Adapter {

    final ProjectService projectService;

    protected Adapter(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Async method used to check if user has access to specified
     * data by checking if they are members of the project it belongs to.
     *
     * @param projectId project to check that user belongs to
     * @param supplier  method for retrieving requested data
     * @param <T> type of data requested
     * @return requested data
     */
    public <T> T checkIfMemberOfProjectAsync(String projectId, Supplier<T> supplier) {
        var voidFuture = projectService.existsByIdAndMemberAsync(projectId);

        var suppliedFuture = CompletableFuture.supplyAsync(supplier);

        CompletableFuture.allOf(voidFuture, suppliedFuture).join();

        try {
            return suppliedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("multithreading error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
