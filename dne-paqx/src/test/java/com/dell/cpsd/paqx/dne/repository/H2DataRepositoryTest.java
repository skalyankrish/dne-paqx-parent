/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.ComponentDetails;
import com.dell.cpsd.paqx.dne.domain.CredentialDetails;
import com.dell.cpsd.paqx.dne.domain.DneJob;
import com.dell.cpsd.paqx.dne.domain.EndpointDetails;
import com.dell.cpsd.paqx.dne.domain.inventory.NodeInventory;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The tests for H2DataRepository.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class H2DataRepositoryTest
{
    @Mock
    private EntityManager entityManager;

    @Mock
    private ComponentDetails componentDetails;

    @Mock
    private EndpointDetails endpointDetails;

    @Mock
    private CredentialDetails credentialDetails;

    @Mock
    private VCenter vCenterData;

    @Mock
    private DneJob dneJob;

    @Mock
    private Host host;

    @Mock
    private ScaleIOData scaleIOData;

    @Mock
    private PortGroup portGroup;

    @Mock
    private PciDevice pciDevice;

    @Mock
    private HostDnsConfig hostDnsConfig;

    @Mock
    private TypedQuery<ComponentDetails> componentDetailsTypedQuery;

    @Mock
    private TypedQuery<VCenter> vCenterTypedQuery;

    @Mock
    private TypedQuery<DneJob> dneJobTypedQuery;

    @Mock
    private TypedQuery<Host> hostTypedQuery;

    @Mock
    private TypedQuery<PortGroup> portGroupTypedQuery;

    @Mock
    private TypedQuery<ScaleIOData> scaleIODataTypedQuery;

    @Mock
    private TypedQuery<PciDevice> pciDeviceTypedQuery;

    @Mock
    private TypedQuery<String> stringTypedQuery;

    @Mock
    private TypedQuery<HostDnsConfig> hostDnsConfigTypedQuery;

    @InjectMocks
    private H2DataRepository repository = new H2DataRepository();

    private Answer                  answer;
    private List<ComponentDetails>  componentDetailsList;
    private List<EndpointDetails>   endpointDetailsList;
    private List<CredentialDetails> credentialDetailsList;
    private List<VCenter>           vCenterList;
    private List<ScaleIOData>       scaleIODataList;
    private List<PortGroup>         portGroupList;
    private List<PciDevice>         pciDeviceList;
    private List<HostDnsConfig>     hostDnsConfigs;
    private String                  componentType;
    private String                  endpointType;
    private String                  componentUUID;
    private String                  endpointUUID;
    private String                  credentialUUID;
    private String                  endpointURL;
    private String                  jobId;
    private Long                    uuid;
    private String                  hostName;
    private String                  clusterName;
    private String                  vNicDevice;

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp() throws Exception
    {
        this.answer = invocation -> {
            DneJob job = invocation.getArgument(0);
            ReflectionTestUtils.setField(job, "uuid", this.uuid);
            return job;
        };

        this.componentDetailsList = new ArrayList<>();
        this.componentDetailsList.add(this.componentDetails);

        this.endpointDetailsList = new ArrayList<>();
        this.endpointDetailsList.add(this.endpointDetails);

        this.credentialDetailsList = new ArrayList<>();
        this.credentialDetailsList.add(this.credentialDetails);

        this.vCenterList = new ArrayList<>();
        this.vCenterList.add(this.vCenterData);

        this.scaleIODataList = new ArrayList<>();
        this.scaleIODataList.add(this.scaleIOData);

        this.portGroupList = new ArrayList<>();
        this.portGroupList.add(this.portGroup);

        this.pciDeviceList = new ArrayList<>();
        this.pciDeviceList.add(this.pciDevice);

        this.hostDnsConfigs = new ArrayList<>();
        this.hostDnsConfigs.add(this.hostDnsConfig);

        this.componentType = "the_component_type";
        this.endpointType = "the_endpoint_type";
        this.componentUUID = UUID.randomUUID().toString();
        this.endpointUUID = UUID.randomUUID().toString();
        this.credentialUUID = UUID.randomUUID().toString();
        this.endpointURL = "http://www.example.com:9999";
        this.jobId = "the_job_id";
        this.uuid = 123456789L;
        this.hostName = "the_host_name";
        this.clusterName = "the_cluster_name";
        this.vNicDevice = "vmk0";
    }

    @Test
    public void saveScaleIoComponentDetails_persist_case() throws Exception
    {
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.componentDetailsTypedQuery).getResultList();
        doNothing().when(this.entityManager).persist(any());

        assertTrue(this.repository.saveScaleIoComponentDetails(componentDetailsList));
        verify(this.entityManager).persist(this.componentDetails);
    }

    @Test
    public void saveScaleIoComponentDetails_merge_case() throws Exception
    {
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();
        doReturn(this.componentDetails).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveScaleIoComponentDetails(componentDetailsList));
        verify(this.entityManager).merge(this.componentDetails);
        verify(this.entityManager).flush();
    }

    @Test
    public void saveScaleIoComponentDetails_empty_component_details_list_case() throws Exception
    {
        assertFalse(this.repository.saveScaleIoComponentDetails(Collections.emptyList()));
        verify(this.entityManager, never()).merge(this.componentDetails);
        verify(this.entityManager, never()).flush();
    }

    @Test
    public void saveScaleIoComponentDetails_exception_case() throws Exception
    {
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.componentDetailsTypedQuery).getResultList();
        doThrow(new IllegalStateException("aaaah")).when(this.entityManager).persist(any());

        assertTrue(this.repository.saveScaleIoComponentDetails(this.componentDetailsList));
        verify(this.entityManager, never()).merge(this.componentDetails);
        verify(this.entityManager, never()).flush();
    }

    @Test
    public void saveVCenterComponentDetails_persist_case() throws Exception
    {
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.componentDetailsTypedQuery).getResultList();
        doNothing().when(this.entityManager).persist(any());

        assertTrue(this.repository.saveVCenterComponentDetails(componentDetailsList));
        verify(this.entityManager).persist(this.componentDetails);
    }

    @Test
    public void saveVCenterComponentDetails_merge_case() throws Exception
    {
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();
        doReturn(this.componentDetails).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveVCenterComponentDetails(componentDetailsList));
        verify(this.entityManager).merge(this.componentDetails);
        verify(this.entityManager).flush();
    }

    @Test
    public void saveVCenterComponentDetails_empty_component_details_list_case() throws Exception
    {
        assertFalse(this.repository.saveVCenterComponentDetails(Collections.emptyList()));
        verify(this.entityManager, never()).merge(this.componentDetails);
        verify(this.entityManager, never()).flush();
    }

    @Test
    public void saveVCenterComponentDetails_exception_case() throws Exception
    {
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.componentDetailsTypedQuery).getResultList();
        doThrow(new IllegalStateException("aaaah")).when(this.entityManager).persist(any());

        assertTrue(this.repository.saveVCenterComponentDetails(this.componentDetailsList));
        verify(this.entityManager, never()).merge(this.componentDetails);
        verify(this.entityManager, never()).flush();
    }

    @Test
    public void getComponentEndpointIds() throws Exception
    {
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();
        doReturn(this.credentialDetailsList).when(this.endpointDetails).getCredentialDetailsList();
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.endpointUUID).when(this.endpointDetails).getEndpointUuid();
        doReturn(this.credentialUUID).when(this.credentialDetails).getCredentialUuid();
        doReturn(this.endpointURL).when(this.endpointDetails).getEndpointUrl();

        assertNotNull(this.repository.getComponentEndpointIds(this.componentType));
    }

    @Test
    public void getComponentEndpointIds_component_details_list_is_empty() throws Exception
    {
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.componentDetailsTypedQuery).getResultList();

        assertNull(this.repository.getComponentEndpointIds(this.componentType));
    }

    @Test
    public void getComponentEndpointIds_component_details_is_null() throws Exception
    {
        this.componentDetailsList.set(0, null);

        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();

        assertNull(this.repository.getComponentEndpointIds(this.componentType));
    }

    @Test
    public void getComponentEndpointIds_endpoint_details_is_null() throws Exception
    {
        this.endpointDetailsList.set(0, null);

        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();

        assertNull(this.repository.getComponentEndpointIds(this.componentType));
    }

    @Test
    public void getComponentEndpointIds_credential_details_list_is_empty() throws Exception
    {
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();
        doReturn(Collections.emptyList()).when(this.endpointDetails).getCredentialDetailsList();

        assertNull(this.repository.getComponentEndpointIds(this.componentType));
    }

    @Test
    public void getComponentEndpointIds_credential_details_is_null() throws Exception
    {
        this.credentialDetailsList.set(0, null);

        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetailsList).when(this.componentDetailsTypedQuery).getResultList();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();
        doReturn(this.credentialDetailsList).when(this.endpointDetails).getCredentialDetailsList();

        assertNull(this.repository.getComponentEndpointIds(this.componentType));
    }

    @Test
    public void getVCenterComponentEndpointIdsByEndpointType() throws Exception
    {
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetails).when(this.componentDetailsTypedQuery).getSingleResult();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();
        doReturn(this.credentialDetailsList).when(this.endpointDetails).getCredentialDetailsList();
        doReturn(this.componentUUID).when(this.componentDetails).getComponentUuid();
        doReturn(this.endpointUUID).when(this.endpointDetails).getEndpointUuid();
        doReturn(this.credentialUUID).when(this.credentialDetails).getCredentialUuid();
        doReturn(this.endpointURL).when(this.endpointDetails).getEndpointUrl();

        assertNotNull(this.repository.getVCenterComponentEndpointIdsByEndpointType(this.endpointType));
    }

    @Test
    public void getVCenterComponentEndpointIdsByEndpointType_endpoint_details_is_null() throws Exception
    {
        this.endpointDetailsList.set(0, null);

        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetails).when(this.componentDetailsTypedQuery).getSingleResult();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();

        assertNull(this.repository.getVCenterComponentEndpointIdsByEndpointType(this.endpointType));
    }

    @Test
    public void getVCenterComponentEndpointIdsByEndpointType_credential_details_list_is_empty() throws Exception
    {
        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetails).when(this.componentDetailsTypedQuery).getSingleResult();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();
        doReturn(Collections.emptyList()).when(this.endpointDetails).getCredentialDetailsList();

        assertNull(this.repository.getVCenterComponentEndpointIdsByEndpointType(this.endpointType));
    }

    @Test
    public void getVCenterComponentEndpointIdsByEndpointType_credential_details_is_null() throws Exception
    {
        this.credentialDetailsList.set(0, null);

        doReturn(this.componentDetailsTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.componentDetails).when(this.componentDetailsTypedQuery).getSingleResult();
        doReturn(this.endpointDetailsList).when(this.componentDetails).getEndpointDetails();
        doReturn(this.credentialDetailsList).when(this.endpointDetails).getCredentialDetailsList();

        assertNull(this.repository.getVCenterComponentEndpointIdsByEndpointType(this.endpointType));
    }

    @Test
    public void saveVCenterData_vcenter_list_is_not_empty() throws Exception
    {
        doReturn(this.vCenterTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.vCenterList).when(this.vCenterTypedQuery).getResultList();

        assertTrue(this.repository.saveVCenterData(this.jobId, this.vCenterData));
    }

    @Test
    public void saveVCenterData_vcenter_list_is_empty_and_dnejob_is_null() throws Exception
    {
        DneJob nullDneJob = null;

        doReturn(this.vCenterTypedQuery, this.dneJobTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.vCenterTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(nullDneJob).when(this.dneJobTypedQuery).getSingleResult();
        doAnswer(this.answer).when(this.entityManager).persist(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveVCenterData(this.jobId, this.vCenterData));
        verify(this.entityManager).persist(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveVCenterData_vcenter_list_is_empty_and_dnejob_is_not_null_and_vcenter_exists() throws Exception
    {
        doReturn(this.vCenterTypedQuery, this.dneJobTypedQuery, this.vCenterTypedQuery).when(this.entityManager)
                .createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.vCenterTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(this.dneJob).when(this.dneJobTypedQuery).getSingleResult();
        doReturn(this.vCenterData).when(this.vCenterTypedQuery).getSingleResult();
        doReturn(this.uuid).when(this.vCenterData).getUuid();
        doReturn(this.vCenterData).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveVCenterData(this.jobId, this.vCenterData));
        verify(this.entityManager).merge(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveVCenterData_vcenter_list_is_empty_and_dnejob_is_not_null_and_vcenter_does_not_exist() throws Exception
    {
        VCenter nullVCenter = null;

        doReturn(this.vCenterTypedQuery, this.dneJobTypedQuery, this.vCenterTypedQuery).when(this.entityManager)
                .createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.vCenterTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(this.dneJob).when(this.dneJobTypedQuery).getSingleResult();
        doReturn(nullVCenter).when(this.vCenterTypedQuery).getSingleResult();
        doReturn(this.dneJob).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();
        doReturn(this.uuid).when(this.dneJob).getUuid();

        assertTrue(this.repository.saveVCenterData(this.jobId, this.vCenterData));
        verify(this.entityManager).merge(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveVCenterData_vcenter_list_exception() throws Exception
    {
        doReturn(this.vCenterTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doThrow(new IllegalStateException("vcenter_list_exception")).when(this.vCenterTypedQuery).getResultList();

        assertFalse(this.repository.saveVCenterData(this.jobId, this.vCenterData));
    }

    @Test
    public void saveVCenterData_dnejob_exception() throws Exception
    {
        doReturn(this.vCenterTypedQuery, this.dneJobTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.vCenterTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doThrow(new NoResultException("dnejob_not_found")).when(this.dneJobTypedQuery).getSingleResult();
        doAnswer(this.answer).when(this.entityManager).persist(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveVCenterData(this.jobId, this.vCenterData));
        verify(this.entityManager).persist(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveVCenterData_vcenter_exception() throws Exception
    {
        doReturn(this.vCenterTypedQuery, this.dneJobTypedQuery, this.vCenterTypedQuery).when(this.entityManager)
                .createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.vCenterTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(this.dneJob).when(this.dneJobTypedQuery).getSingleResult();
        doThrow(new IllegalStateException("vcenter_not_found")).when(this.vCenterTypedQuery).getSingleResult();
        doReturn(this.dneJob).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();
        doReturn(this.uuid).when(this.dneJob).getUuid();

        assertTrue(this.repository.saveVCenterData(this.jobId, this.vCenterData));
        verify(this.entityManager).merge(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveScaleIoData_scaleiodata_list_is_not_empty() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.scaleIODataList).when(this.scaleIODataTypedQuery).getResultList();

        assertTrue(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
    }

    @Test
    public void saveScaleIoData_scaleiodata_list_is_empty_and_dnejob_is_null() throws Exception
    {
        DneJob nullDneJob = null;

        doReturn(this.scaleIODataTypedQuery, this.dneJobTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(nullDneJob).when(this.dneJobTypedQuery).getSingleResult();
        doAnswer(this.answer).when(this.entityManager).persist(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
        verify(this.entityManager).persist(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveScaleIoData_scaleiodata_list_is_empty_and_dnejob_is_not_null_and_scaliodata_exists() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery, this.dneJobTypedQuery, this.scaleIODataTypedQuery).when(this.entityManager)
                .createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(this.dneJob).when(this.dneJobTypedQuery).getSingleResult();
        doReturn(this.scaleIOData).when(this.scaleIODataTypedQuery).getSingleResult();
        doReturn(this.uuid).when(this.scaleIOData).getUuid();
        doReturn(this.scaleIOData).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
        verify(this.entityManager).merge(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveScaleIoData_scaleiodata_list_is_empty_and_dnejob_is_not_null_and_scaleiodata_does_not_exist() throws Exception
    {
        ScaleIOData nullScaleIOData = null;

        doReturn(this.scaleIODataTypedQuery, this.dneJobTypedQuery, this.scaleIODataTypedQuery).when(this.entityManager)
                .createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(this.dneJob).when(this.dneJobTypedQuery).getSingleResult();
        doReturn(nullScaleIOData).when(this.scaleIODataTypedQuery).getSingleResult();
        doReturn(this.dneJob).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
        verify(this.entityManager).merge(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveScaleIoData_scaleiodata_list_exception() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doThrow(new IllegalStateException("scaleiodata_list_exception")).when(this.scaleIODataTypedQuery).getResultList();

        assertFalse(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
    }

    @Test
    public void saveScaleIoData_dnejob_exception() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery, this.dneJobTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doThrow(new NoResultException("dnejob_not_found")).when(this.dneJobTypedQuery).getSingleResult();
        doAnswer(this.answer).when(this.entityManager).persist(any());
        doNothing().when(this.entityManager).flush();

        assertTrue(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
        verify(this.entityManager).persist(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void saveScaleIoData_scaleiodata_exception() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery, this.dneJobTypedQuery, this.scaleIODataTypedQuery).when(this.entityManager)
                .createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();
        doReturn(this.dneJobTypedQuery).when(this.dneJobTypedQuery).setParameter(anyString(), anyString());
        doReturn(this.dneJob).when(this.dneJobTypedQuery).getSingleResult();
        doThrow(new IllegalStateException("scaleiodata_not_found")).when(this.scaleIODataTypedQuery).getSingleResult();
        doReturn(this.dneJob).when(this.entityManager).merge(any());
        doNothing().when(this.entityManager).flush();
        doReturn(this.uuid).when(this.dneJob).getUuid();

        assertTrue(this.repository.saveScaleIoData(this.jobId, this.scaleIOData));
        verify(this.entityManager).merge(any());
        verify(this.entityManager).flush();
    }

    @Test
    public void getVCenterHost() throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.hostTypedQuery).when(this.hostTypedQuery).setParameter(anyString(), eq(this.hostName));
        doReturn(this.host).when(this.hostTypedQuery).getSingleResult();

        assertNotNull(this.repository.getVCenterHost(this.hostName));
    }

    @Test
    public void getExistingVCenterHost() throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Arrays.asList(this.host)).when(this.hostTypedQuery).getResultList();

        assertNotNull(this.repository.getExistingVCenterHost());
    }

    @Test
    public void getExistingVCenterHost_should_throw_an_exception_if_no_existing_vcenter_host_found() throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.hostTypedQuery).getResultList();

        try
        {
            this.repository.getExistingVCenterHost();
            fail("Expected an exception to be thrown here but it wasn't");
        }
        catch (Exception ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("no host"));
        }
    }

    @Test
    public void getPortGroups() throws Exception
    {
        doReturn(this.portGroupTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.portGroupList).when(this.portGroupTypedQuery).getResultList();

        assertNotNull(this.repository.getPortGroups());
    }

    @Test
    public void getScaleIoDataByJobId_scaleio_list_not_empty() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.scaleIODataTypedQuery).when(this.scaleIODataTypedQuery).setParameter(anyString(), eq(this.jobId));
        doReturn(this.scaleIODataList).when(this.scaleIODataTypedQuery).getResultList();

        assertNotNull(this.repository.getScaleIoDataByJobId(this.jobId));
    }

    @Test
    public void getScaleIoDataByJobId_scaleio_list_empty() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.scaleIODataTypedQuery).when(this.scaleIODataTypedQuery).setParameter(anyString(), eq(this.jobId));
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();

        assertNull(this.repository.getScaleIoDataByJobId(this.jobId));
    }

    @Test
    public void getScaleIoData_scaleio_list_not_empty() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.scaleIODataList).when(this.scaleIODataTypedQuery).getResultList();

        assertNotNull(this.repository.getScaleIoData());
    }

    @Test
    public void getScaleIoData_scaleio_list_empty() throws Exception
    {
        doReturn(this.scaleIODataTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.scaleIODataTypedQuery).getResultList();

        assertNull(this.repository.getScaleIoData());
    }

    @Test
    public void getPciDeviceList() throws Exception
    {
        doReturn(this.pciDeviceTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.pciDeviceList).when(this.pciDeviceTypedQuery).getResultList();

        assertNotNull(this.repository.getPciDeviceList());
    }

    @Test
    public void getClusterId() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.stringTypedQuery).when(this.stringTypedQuery).setParameter(anyString(), eq(this.clusterName));
        doReturn(UUID.randomUUID().toString()).when(this.stringTypedQuery).getSingleResult();

        assertNotNull(this.repository.getClusterId(this.clusterName));
    }

    @Test
    public void getClusterId_exception_thrown() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.stringTypedQuery).when(this.stringTypedQuery).setParameter(anyString(), eq(this.clusterName));
        doThrow(new IllegalStateException(("aaaaaaah"))).when(this.stringTypedQuery).getSingleResult();

        assertNull(this.repository.getClusterId(this.clusterName));
    }

    @Test
    public void getDataCenterName() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.stringTypedQuery).when(this.stringTypedQuery).setParameter(anyString(), eq(this.clusterName));
        doReturn(UUID.randomUUID().toString()).when(this.stringTypedQuery).getSingleResult();

        assertNotNull(this.repository.getDataCenterName(this.clusterName));
    }

    @Test
    public void getDataCenterName_exception_thrown() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.stringTypedQuery).when(this.stringTypedQuery).setParameter(anyString(), eq(this.clusterName));
        doThrow(new IllegalStateException(("aaaaaaah"))).when(this.stringTypedQuery).getSingleResult();

        assertNull(this.repository.getDataCenterName(this.clusterName));
    }

    @Test
    public void getVlanIdSuccess() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(UUID.randomUUID().toString()).when(this.stringTypedQuery).getSingleResult();

        assertNotNull(this.repository.getVlanIdVmk0());
    }

    @Test
    public void getVlanIdExceptionThrown() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doThrow(new NoResultException("Exception exception")).when(this.stringTypedQuery).getSingleResult();

        assertNull(this.repository.getVlanIdVmk0());
    }

    @Test
    public void getValidNodeInventory() throws Exception
    {
        NodeInventory nodeInventory = new NodeInventory("FAKE_KEY", "FAKE_INVENTORY");

        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(nodeInventory).when(this.stringTypedQuery).getSingleResult();

        NodeInventory nodeInventoryFound = this.repository.getNodeIventory("FAKE_KEY");

        assertNotNull(nodeInventoryFound);
        assertEquals("FAKE_KEY", nodeInventoryFound.getSymphonyUuid());
        assertEquals("FAKE_INVENTORY", nodeInventoryFound.getNodeInventory());
    }

    @Test
    public void getInvalidNodeInventory() throws Exception
    {
        doReturn(this.stringTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(null).when(this.stringTypedQuery).getSingleResult();

        NodeInventory nodeInventoryFound = this.repository.getNodeIventory("FAKE_KEY");

        assertNull(nodeInventoryFound);
    }

    @Test
    public void saveNullNodeInventory() throws Exception
    {
        boolean result = this.repository.saveNodeInventory(null);

        assertEquals(false, result);
    }

    @Test
    public void saveValidNodeInventory() throws Exception
    {
        NodeInventory nodeInventory = new NodeInventory("FAKE_KEY", "FAKE_INVENTORY");

        DataServiceRepository repositorySpy = Mockito.spy(this.repository);
        doReturn(nodeInventory).when(repositorySpy).getNodeIventory(anyString());

        doNothing().when(this.entityManager).flush();
        doNothing().when(this.entityManager).persist(any());
        doNothing().when(this.entityManager).remove(any());

        boolean result =repositorySpy.saveNodeInventory(nodeInventory);

        assertEquals(true, result);
        verify(this.entityManager).flush();
        verify(this.entityManager).persist(any());
        verify(this.entityManager).remove(any());
    }

    @Test
    public void saveNoExistentNodeInventory() throws Exception
    {
        NodeInventory nodeInventory = new NodeInventory("FAKE_KEY", "FAKE_INVENTORY");

        DataServiceRepository repositorySpy = Mockito.spy(this.repository);
        doReturn(null).when(repositorySpy).getNodeIventory(anyString());

        doNothing().when(this.entityManager).flush();
        doNothing().when(this.entityManager).persist(any());

        boolean result =repositorySpy.saveNodeInventory(nodeInventory);

        assertEquals(true, result);
        verify(this.entityManager).flush();
        verify(this.entityManager).persist(any());
        verify(this.entityManager, times(0)).remove(any());
    }

    @Test
    public void getDomainName_should_return_the_host_dns_config_domain_name_if_its_not_empty()  throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.hostDnsConfigs).when(this.hostTypedQuery).getResultList();
        doReturn("domain1").when(this.hostDnsConfig).getDomainName();

        String result = this.repository.getDomainName();

        assertThat(result, is("domain1"));
    }

    @Test
    public void getDomainName_should_return_the_domain_name_from_the_list_of_search_domains()  throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.hostDnsConfigs).when(this.hostTypedQuery).getResultList();
        doReturn(Arrays.asList("domain1", "domain2", "domain3")).when(this.hostDnsConfig).getSearchDomains();

        String result = this.repository.getDomainName();

        assertThat(result, is("domain1"));
    }

    @Test
    public void getDomainName_should_return_null_when_the_host_dns_query_returns_null()  throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(null).when(this.hostTypedQuery).getResultList();

        String result = this.repository.getDomainName();

        assertNull(result);
    }

    @Test
    public void getDomainName_should_return_null_if_there_are_no_host_dns_configs()  throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(Collections.emptyList()).when(this.hostTypedQuery).getResultList();

        String result = this.repository.getDomainName();

        assertNull(result);
    }

    @Test
    public void getDomainName_should_return_null_if_there_are_no_search_domains()  throws Exception
    {
        doReturn(this.hostTypedQuery).when(this.entityManager).createQuery(anyString(), any());
        doReturn(this.hostDnsConfigs).when(this.hostTypedQuery).getResultList();
        doReturn(Collections.emptyList()).when(this.hostDnsConfig).getSearchDomains();

        String result = this.repository.getDomainName();

        assertNull(result);
    }
}