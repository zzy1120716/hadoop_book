//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.hadoop.mapred;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Cluster.JobTrackerStatus;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.JobStatus.State;
import org.apache.hadoop.mapreduce.QueueAclsInfo;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.TaskReport;
import org.apache.hadoop.mapreduce.protocol.ClientProtocol;
import org.apache.hadoop.mapreduce.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.mapreduce.v2.LogParams;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenSelector;
import org.apache.hadoop.yarn.util.ConverterUtils;

public class YARNRunner implements ClientProtocol {
    private static final Log LOG = LogFactory.getLog(YARNRunner.class);
    private final RecordFactory recordFactory;
    private ResourceMgrDelegate resMgrDelegate;
    private ClientCache clientCache;
    private Configuration conf;
    private final FileContext defaultFileContext;

    public YARNRunner(Configuration conf) {
        this(conf, new ResourceMgrDelegate(new YarnConfiguration(conf)));
    }

    public YARNRunner(Configuration conf, ResourceMgrDelegate resMgrDelegate) {
        this(conf, resMgrDelegate, new ClientCache(conf, resMgrDelegate));
    }

    public YARNRunner(Configuration conf, ResourceMgrDelegate resMgrDelegate, ClientCache clientCache) {
        this.recordFactory = RecordFactoryProvider.getRecordFactory((Configuration)null);
        this.conf = conf;

        try {
            this.resMgrDelegate = resMgrDelegate;
            this.clientCache = clientCache;
            this.defaultFileContext = FileContext.getFileContext(this.conf);
        } catch (UnsupportedFileSystemException var5) {
            throw new RuntimeException("Error in instantiating YarnClient", var5);
        }
    }

    @Private
    public void setResourceMgrDelegate(ResourceMgrDelegate resMgrDelegate) {
        this.resMgrDelegate = resMgrDelegate;
    }

    public void cancelDelegationToken(Token<DelegationTokenIdentifier> arg0) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Use Token.renew instead");
    }

    public TaskTrackerInfo[] getActiveTrackers() throws IOException, InterruptedException {
        return this.resMgrDelegate.getActiveTrackers();
    }

    public JobStatus[] getAllJobs() throws IOException, InterruptedException {
        return this.resMgrDelegate.getAllJobs();
    }

    public TaskTrackerInfo[] getBlacklistedTrackers() throws IOException, InterruptedException {
        return this.resMgrDelegate.getBlacklistedTrackers();
    }

    public ClusterMetrics getClusterMetrics() throws IOException, InterruptedException {
        return this.resMgrDelegate.getClusterMetrics();
    }

    @VisibleForTesting
    void addHistoryToken(Credentials ts) throws IOException, InterruptedException {
        MRClientProtocol hsProxy = this.clientCache.getInitializedHSProxy();
        if (UserGroupInformation.isSecurityEnabled() && hsProxy != null) {
            RMDelegationTokenSelector tokenSelector = new RMDelegationTokenSelector();
            Text service = this.resMgrDelegate.getRMDelegationTokenService();
            if (tokenSelector.selectToken(service, ts.getAllTokens()) != null) {
                Text hsService = SecurityUtil.buildTokenService(hsProxy.getConnectAddress());
                if (ts.getToken(hsService) == null) {
                    ts.addToken(hsService, this.getDelegationTokenFromHS(hsProxy));
                }
            }
        }

    }

    @VisibleForTesting
    Token<?> getDelegationTokenFromHS(MRClientProtocol hsProxy) throws IOException, InterruptedException {
        GetDelegationTokenRequest request = (GetDelegationTokenRequest)this.recordFactory.newRecordInstance(GetDelegationTokenRequest.class);
        request.setRenewer(Master.getMasterPrincipal(this.conf));
        org.apache.hadoop.yarn.api.records.Token mrDelegationToken = hsProxy.getDelegationToken(request).getDelegationToken();
        return ConverterUtils.convertFromYarn(mrDelegationToken, hsProxy.getConnectAddress());
    }

    public Token<DelegationTokenIdentifier> getDelegationToken(Text renewer) throws IOException, InterruptedException {
        return this.resMgrDelegate.getDelegationToken(renewer);
    }

    public String getFilesystemName() throws IOException, InterruptedException {
        return this.resMgrDelegate.getFilesystemName();
    }

    public JobID getNewJobID() throws IOException, InterruptedException {
        return this.resMgrDelegate.getNewJobID();
    }

    public QueueInfo getQueue(String queueName) throws IOException, InterruptedException {
        return this.resMgrDelegate.getQueue(queueName);
    }

    public QueueAclsInfo[] getQueueAclsForCurrentUser() throws IOException, InterruptedException {
        return this.resMgrDelegate.getQueueAclsForCurrentUser();
    }

    public QueueInfo[] getQueues() throws IOException, InterruptedException {
        return this.resMgrDelegate.getQueues();
    }

    public QueueInfo[] getRootQueues() throws IOException, InterruptedException {
        return this.resMgrDelegate.getRootQueues();
    }

    public QueueInfo[] getChildQueues(String parent) throws IOException, InterruptedException {
        return this.resMgrDelegate.getChildQueues(parent);
    }

    public String getStagingAreaDir() throws IOException, InterruptedException {
        return this.resMgrDelegate.getStagingAreaDir();
    }

    public String getSystemDir() throws IOException, InterruptedException {
        return this.resMgrDelegate.getSystemDir();
    }

    public long getTaskTrackerExpiryInterval() throws IOException, InterruptedException {
        return this.resMgrDelegate.getTaskTrackerExpiryInterval();
    }

    public JobStatus submitJob(JobID jobId, String jobSubmitDir, Credentials ts) throws IOException, InterruptedException {
        this.addHistoryToken(ts);
        ApplicationSubmissionContext appContext = this.createApplicationSubmissionContext(this.conf, jobSubmitDir, ts);

        try {
            ApplicationId applicationId = this.resMgrDelegate.submitApplication(appContext);
            ApplicationReport appMaster = this.resMgrDelegate.getApplicationReport(applicationId);
            String diagnostics = appMaster == null ? "application report is null" : appMaster.getDiagnostics();
            if (appMaster != null && appMaster.getYarnApplicationState() != YarnApplicationState.FAILED && appMaster.getYarnApplicationState() != YarnApplicationState.KILLED) {
                return this.clientCache.getClient(jobId).getJobStatus(jobId);
            } else {
                throw new IOException("Failed to run job : " + diagnostics);
            }
        } catch (YarnException var8) {
            throw new IOException(var8);
        }
    }

    private LocalResource createApplicationResource(FileContext fs, Path p, LocalResourceType type) throws IOException {
        LocalResource rsrc = (LocalResource)this.recordFactory.newRecordInstance(LocalResource.class);
        FileStatus rsrcStat = fs.getFileStatus(p);
        rsrc.setResource(ConverterUtils.getYarnUrlFromPath(fs.getDefaultFileSystem().resolvePath(rsrcStat.getPath())));
        rsrc.setSize(rsrcStat.getLen());
        rsrc.setTimestamp(rsrcStat.getModificationTime());
        rsrc.setType(type);
        rsrc.setVisibility(LocalResourceVisibility.APPLICATION);
        return rsrc;
    }

    public ApplicationSubmissionContext createApplicationSubmissionContext(Configuration jobConf, String jobSubmitDir, Credentials ts) throws IOException {
        ApplicationId applicationId = this.resMgrDelegate.getApplicationId();
        Resource capability = (Resource)this.recordFactory.newRecordInstance(Resource.class);
        capability.setMemory(this.conf.getInt("yarn.app.mapreduce.am.resource.mb", 1536));
        capability.setVirtualCores(this.conf.getInt("yarn.app.mapreduce.am.resource.cpu-vcores", 1));
        LOG.debug("AppMaster capability = " + capability);
        Map<String, LocalResource> localResources = new HashMap();
        Path jobConfPath = new Path(jobSubmitDir, "job.xml");
        URL yarnUrlForJobSubmitDir = ConverterUtils.getYarnUrlFromPath(this.defaultFileContext.getDefaultFileSystem().resolvePath(this.defaultFileContext.makeQualified(new Path(jobSubmitDir))));
        LOG.debug("Creating setup context, jobSubmitDir url is " + yarnUrlForJobSubmitDir);
        localResources.put("job.xml", this.createApplicationResource(this.defaultFileContext, jobConfPath, LocalResourceType.FILE));
        if (jobConf.get("mapreduce.job.jar") != null) {
            Path jobJarPath = new Path(jobConf.get("mapreduce.job.jar"));
            LocalResource rc = this.createApplicationResource(this.defaultFileContext, jobJarPath, LocalResourceType.PATTERN);
            String pattern = this.conf.getPattern("mapreduce.job.jar.unpack.pattern", JobConf.UNPACK_JAR_PATTERN_DEFAULT).pattern();
            rc.setPattern(pattern);
            localResources.put("job.jar", rc);
        } else {
            LOG.info("Job jar is not present. Not adding any jar to the list of resources.");
        }

        String[] arr$ = new String[]{"job.split", "job.splitmetainfo"};
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            localResources.put("jobSubmitDir/" + s, this.createApplicationResource(this.defaultFileContext, new Path(jobSubmitDir, s), LocalResourceType.FILE));
        }

        DataOutputBuffer dob = new DataOutputBuffer();
        ts.writeTokenStorageToStream(dob);
        ByteBuffer securityTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        List<String> vargs = new ArrayList(8);
        // TODO 注释原来的代码
        // vargs.add(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME) + "/bin/java");
        // TODO 修改环境配置，从而在windows本地执行mapreduce任务
        System.out.println(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME) + "/bin/java");
        System.out.println("$JAVA_HOME/bin/java");
        vargs.add("$JAVA_HOME/bin/java");

        long logSize = jobConf.getLong("yarn.app.mapreduce.am.container.log.limit.kb", 0L) << 10;
        String logLevel = jobConf.get("yarn.app.mapreduce.am.log.level", "INFO");
        int numBackups = jobConf.getInt("yarn.app.mapreduce.am.container.log.backups", 0);
        MRApps.addLog4jSystemProperties(logLevel, logSize, numBackups, vargs);
        warnForJavaLibPath(this.conf.get("mapreduce.map.java.opts", ""), "map", "mapreduce.map.java.opts", "mapreduce.map.env");
        warnForJavaLibPath(this.conf.get("mapreduce.admin.map.child.java.opts", ""), "map", "mapreduce.admin.map.child.java.opts", "mapreduce.admin.user.env");
        warnForJavaLibPath(this.conf.get("mapreduce.reduce.java.opts", ""), "reduce", "mapreduce.reduce.java.opts", "mapreduce.reduce.env");
        warnForJavaLibPath(this.conf.get("mapreduce.admin.reduce.child.java.opts", ""), "reduce", "mapreduce.admin.reduce.child.java.opts", "mapreduce.admin.user.env");
        String mrAppMasterAdminOptions = this.conf.get("yarn.app.mapreduce.am.admin-command-opts", "");
        warnForJavaLibPath(mrAppMasterAdminOptions, "app master", "yarn.app.mapreduce.am.admin-command-opts", "yarn.app.mapreduce.am.admin.user.env");
        vargs.add(mrAppMasterAdminOptions);
        String mrAppMasterUserOptions = this.conf.get("yarn.app.mapreduce.am.command-opts", "-Xmx1024m");
        warnForJavaLibPath(mrAppMasterUserOptions, "app master", "yarn.app.mapreduce.am.command-opts", "yarn.app.mapreduce.am.env");
        vargs.add(mrAppMasterUserOptions);
        vargs.add("org.apache.hadoop.mapreduce.v2.app.MRAppMaster");
        vargs.add("1><LOG_DIR>/stdout");
        vargs.add("2><LOG_DIR>/stderr");
        Vector<String> vargsFinal = new Vector(8);
        StringBuilder mergedCommand = new StringBuilder();
        Iterator i$ = vargs.iterator();

        while(i$.hasNext()) {
            CharSequence str = (CharSequence)i$.next();
            mergedCommand.append(str).append(" ");
        }

        vargsFinal.add(mergedCommand.toString());
        LOG.debug("Command to launch container for ApplicationMaster is : " + mergedCommand);
        Map<String, String> environment = new HashMap();
        MRApps.setClasspath(environment, this.conf);
        MRApps.setEnvFromInputString(environment, this.conf.get("yarn.app.mapreduce.am.admin.user.env"), this.conf);
        MRApps.setEnvFromInputString(environment, this.conf.get("yarn.app.mapreduce.am.env"), this.conf);
        MRApps.setupDistributedCache(jobConf, localResources);
        Map<ApplicationAccessType, String> acls = new HashMap(2);
        acls.put(ApplicationAccessType.VIEW_APP, jobConf.get("mapreduce.job.acl-view-job", " "));
        acls.put(ApplicationAccessType.MODIFY_APP, jobConf.get("mapreduce.job.acl-modify-job", " "));

        // TODO BY ZZY
        for (String key : environment.keySet()) {
            String org = environment.get(key);
            String linux = getLinux(org);
            environment.put(key, linux);
        }

        ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(localResources, environment, vargsFinal, (Map)null, securityTokens, acls);
        Collection<String> tagsFromConf = jobConf.getTrimmedStringCollection("mapreduce.job.tags");
        ApplicationSubmissionContext appContext = (ApplicationSubmissionContext)this.recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
        appContext.setApplicationId(applicationId);
        appContext.setQueue(jobConf.get("mapreduce.job.queuename", "default"));
        appContext.setApplicationName(jobConf.get("mapreduce.job.name", "N/A"));
        appContext.setCancelTokensWhenComplete(this.conf.getBoolean("mapreduce.job.complete.cancel.delegation.tokens", true));
        appContext.setAMContainerSpec(amContainer);
        appContext.setMaxAppAttempts(this.conf.getInt("mapreduce.am.max-attempts", 2));
        appContext.setResource(capability);
        appContext.setApplicationType("MAPREDUCE");
        if (tagsFromConf != null && !tagsFromConf.isEmpty()) {
            appContext.setApplicationTags(new HashSet(tagsFromConf));
        }

        return appContext;
    }

    // TODO BY ZZY
    private String getLinux(String org) {
        StringBuilder sb = new StringBuilder();
        int c = 0;
        for (int i = 0; i < org.length(); i++) {
            if (org.charAt(i) == '%') {
                c++;
                if (c % 2 == 1) {
                    sb.append("$");
                }
            } else {
                switch (org.charAt(i)) {
                    case ';':
                        sb.append(":");
                        break;

                    case '\\':
                        sb.append("/");
                        break;
                    default:
                        sb.append(org.charAt(i));
                        break;
                }
            }
        }
        return (sb.toString());
    }

    public void setJobPriority(JobID arg0, String arg1) throws IOException, InterruptedException {
        this.resMgrDelegate.setJobPriority(arg0, arg1);
    }

    public long getProtocolVersion(String arg0, long arg1) throws IOException {
        return this.resMgrDelegate.getProtocolVersion(arg0, arg1);
    }

    public long renewDelegationToken(Token<DelegationTokenIdentifier> arg0) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Use Token.renew instead");
    }

    public Counters getJobCounters(JobID arg0) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0).getJobCounters(arg0);
    }

    public String getJobHistoryDir() throws IOException, InterruptedException {
        return JobHistoryUtils.getConfiguredHistoryServerDoneDirPrefix(this.conf);
    }

    public JobStatus getJobStatus(JobID jobID) throws IOException, InterruptedException {
        JobStatus status = this.clientCache.getClient(jobID).getJobStatus(jobID);
        return status;
    }

    public TaskCompletionEvent[] getTaskCompletionEvents(JobID arg0, int arg1, int arg2) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0).getTaskCompletionEvents(arg0, arg1, arg2);
    }

    public String[] getTaskDiagnostics(TaskAttemptID arg0) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0.getJobID()).getTaskDiagnostics(arg0);
    }

    public TaskReport[] getTaskReports(JobID jobID, TaskType taskType) throws IOException, InterruptedException {
        return this.clientCache.getClient(jobID).getTaskReports(jobID, taskType);
    }

    public void killJob(JobID arg0) throws IOException, InterruptedException {
        JobStatus status = this.clientCache.getClient(arg0).getJobStatus(arg0);
        if (status.getState() != State.RUNNING) {
            try {
                this.resMgrDelegate.killApplication(TypeConverter.toYarn(arg0).getAppId());
            } catch (YarnException var8) {
                throw new IOException(var8);
            }
        } else {
            try {
                this.clientCache.getClient(arg0).killJob(arg0);
                long currentTimeMillis = System.currentTimeMillis();

                for(long timeKillIssued = currentTimeMillis; currentTimeMillis < timeKillIssued + 10000L && status.getState() != State.KILLED; status = this.clientCache.getClient(arg0).getJobStatus(arg0)) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var10) {
                        break;
                    }

                    currentTimeMillis = System.currentTimeMillis();
                }
            } catch (IOException var11) {
                LOG.debug("Error when checking for application status", var11);
            }

            if (status.getState() != State.KILLED) {
                try {
                    this.resMgrDelegate.killApplication(TypeConverter.toYarn(arg0).getAppId());
                } catch (YarnException var9) {
                    throw new IOException(var9);
                }
            }

        }
    }

    public boolean killTask(TaskAttemptID arg0, boolean arg1) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0.getJobID()).killTask(arg0, arg1);
    }

    public AccessControlList getQueueAdmins(String arg0) throws IOException {
        return new AccessControlList("*");
    }

    public JobTrackerStatus getJobTrackerStatus() throws IOException, InterruptedException {
        return JobTrackerStatus.RUNNING;
    }

    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return ProtocolSignature.getProtocolSignature(this, protocol, clientVersion, clientMethodsHash);
    }

    public LogParams getLogFileParams(JobID jobID, TaskAttemptID taskAttemptID) throws IOException {
        return this.clientCache.getClient(jobID).getLogFilePath(jobID, taskAttemptID);
    }

    private static void warnForJavaLibPath(String opts, String component, String javaConf, String envConf) {
        if (opts != null && opts.contains("-Djava.library.path")) {
            LOG.warn("Usage of -Djava.library.path in " + javaConf + " can cause " + "programs to no longer function if hadoop native libraries " + "are used. These values should be set as part of the " + "LD_LIBRARY_PATH in the " + component + " JVM env using " + envConf + " config settings.");
        }

    }
}
