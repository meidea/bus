

package org.aoju.bus.gitlab;

import org.aoju.bus.gitlab.models.RegistryRepository;
import org.aoju.bus.gitlab.models.RegistryRepositoryTag;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>This class implements the client side API for the GitLab Container Registry API.
 * See <a href="https://docs.gitlab.com/ee/api/container_registry.html">Container Registry API at GitLab</a>
 * for more information.</p>
 */
public class ContainerRegistryApi extends AbstractApi {

    public ContainerRegistryApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of registry repositories in a project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @return a list of pages in the project's registry repositories
     * @throws GitLabApiException if any exception occurs
     */
    public List<RegistryRepository> getRepositories(Object projectIdOrPath) throws GitLabApiException {
        return (getRepositories(projectIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Get a list of registry repositories in a project that fall within the specified page parameters.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param page            the page to get
     * @param perPage         the number of Package instances per page
     * @return a list of registry repositories for the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<RegistryRepository> getRepositories(Object projectIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage),
                "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories");
        return response.readEntity(new GenericType<List<RegistryRepository>>() {
        });
    }

    /**
     * Get a Pager of registry repositories in a project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param itemsPerPage    the number of RegistryRepository instances per page
     * @return a Pager of registry repositories for the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<RegistryRepository> getRepositories(Object projectIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<>(this, RegistryRepository.class, itemsPerPage, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories"));
    }

    /**
     * Get a Stream of registry repositories in a project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @return a Stream of pages in the project's registry repositories
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<RegistryRepository> getRepositoriesStream(Object projectIdOrPath) throws GitLabApiException {
        return (getRepositories(projectIdOrPath, getDefaultPerPage()).stream());
    }

    /**
     * Delete a repository in registry.
     * <p>
     * This operation is executed asynchronously and might take some time to get executed.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/registry/repositories/:repository_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteRepository(Object projectIdOrPath, Integer repositoryId) throws GitLabApiException {

        if (repositoryId == null) {
            throw new RuntimeException("repositoryId cannot be null");
        }

        delete(Response.Status.NO_CONTENT, null, "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories", repositoryId);
    }

    /**
     * Get a list of tags for given registry repository.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories/:repository_id/tags</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @return a list of Repository Tags for the specified repository ID
     * @throws GitLabApiException if any exception occurs
     */
    public List<RegistryRepositoryTag> getRepositoryTags(Object projectIdOrPath, Integer repositoryId) throws GitLabApiException {
        return getRepositoryTags(projectIdOrPath, repositoryId, getDefaultPerPage()).all();
    }

    /**
     * Get a Pager of tags for given registry repository.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories/:repository_id/tags</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @param itemsPerPage    the number of RegistryRepositoryTag instances per page
     * @return a Pager of Repository Tags for the specified repository ID
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<RegistryRepositoryTag> getRepositoryTags(Object projectIdOrPath, Integer repositoryId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<>(this, RegistryRepositoryTag.class, itemsPerPage, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories", repositoryId, "tags"));
    }

    /**
     * Get a Stream of tags for given registry repository.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories/:repository_id/tags</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @return a list of Repository Tags for the specified repository ID
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<RegistryRepositoryTag> getRepositoryTagsStream(Object projectIdOrPath, Integer repositoryId) throws GitLabApiException {
        return getRepositoryTags(projectIdOrPath, repositoryId, getDefaultPerPage()).stream();
    }

    /**
     * Get details of a registry repository tag.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories/:repository_id/tags/:tag_name</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @param tagName         the name of tag
     * @return the Repository Tag for the specified repository ID
     * @throws GitLabApiException if any exception occurs
     */
    public RegistryRepositoryTag getRepositoryTag(Object projectIdOrPath, Integer repositoryId, String tagName) throws GitLabApiException {
        Response response = get(Response.Status.OK, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories", repositoryId, "tags", tagName);
        return response.readEntity(new GenericType<RegistryRepositoryTag>() {
        });
    }

    /**
     * Get details of a registry repository tag as the value of an Optional.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/registry/repositories/:repository_id/tags/:tag_name</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @param tagName         the name of tag
     * @return the Repository Tag for the specified repository ID as the value of the Optional
     */
    public Optional<RegistryRepositoryTag> getOptionalRepositoryTag(Object projectIdOrPath, Integer repositoryId, String tagName) {
        try {
            return (Optional.ofNullable(getRepositoryTag(projectIdOrPath, repositoryId, tagName)));
        } catch (GitLabApiException glae) {
            return (GitLabApi.createOptionalFromException(glae));
        }
    }

    /**
     * Delete a registry repository tag.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/registry/repositories/:repository_id/tags/:tag_name</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @param tagName         the name of the tag to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteRepositoryTag(Object projectIdOrPath, Integer repositoryId, String tagName) throws GitLabApiException {

        if (repositoryId == null) {
            throw new RuntimeException("repositoryId cannot be null");
        }

        delete(Response.Status.NO_CONTENT, null, "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories", repositoryId, "tags", tagName);
    }

    /**
     * Delete repository tags in bulk based on given criteria.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/registry/repositories/:repository_id/tags</code></pre>
     * <p>
     * This API call performs the following operations:
     * <ol>
     * <li>It orders all tags by creation date. The creation date is the time of the manifest creation,
     * not the time of tag push.</li>
     * <li>It removes only the tags matching the given name_regex.</li>
     * <li>It never removes the tag named latest.</li>
     * <li>It keeps N latest matching tags (if keep_n is specified).</li>
     * <li>It only removes tags that are older than X amount of time (if older_than is specified).</li>
     * <li>It schedules the asynchronous job to be executed in the background.</li>
     * </ol>
     * <p>
     * These operations are executed asynchronously and it might take time to get executed. You can run this at most
     * once an hour for a given container repository.
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param repositoryId    the ID of registry repository
     * @param nameRegex       the regex of the name to delete. To delete all tags specify <code>.*</code>.
     * @param keepN           the amount of latest tags of given name to keep.
     * @param olderThan       tags to delete that are older than the given time, written in human readable form
     *                        <code>1h</code>, <code>1d</code>, <code>1month</code>.
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteRepositoryTags(Object projectIdOrPath, Integer repositoryId, String nameRegex, Integer keepN, String olderThan) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name_regex", nameRegex, true)
                .withParam("keep_n", keepN)
                .withParam("older_than", olderThan);

        delete(Response.Status.NO_CONTENT, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "registry", "repositories", repositoryId, "tags");
    }
}