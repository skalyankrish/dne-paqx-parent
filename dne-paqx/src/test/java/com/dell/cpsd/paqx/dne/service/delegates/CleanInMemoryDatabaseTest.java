/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CleanInMemoryDatabaseTest
{
    private CleanInMemoryDatabase cleanInMemoryDatabase;
    private DelegateExecution     delegateExecution;
    private DataServiceRepository repository;
    private NodeDetail            nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        repository = mock(DataServiceRepository.class);
        cleanInMemoryDatabase = new CleanInMemoryDatabase(repository);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
    }

    @Test
    public void testExceptionThrown1() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(repository.cleanInMemoryDatabase()).willThrow(new NullPointerException());
            cleanInMemoryDatabase.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CLEAN_IN_MEMORY_DATABASE_ERROR));
            assertTrue(error.getMessage().contains("Error cleaning in memory database:"));
        }
    }

    @Test
    public void testExecutionFailed()
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.cleanInMemoryDatabase()).thenReturn(false);
            cleanInMemoryDatabase.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CLEAN_IN_MEMORY_DATABASE_ERROR));
            assertTrue(error.getMessage().contains("Error cleaning in memory database."));
        }
    }

    @Test
    public void testSuccess()
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.cleanInMemoryDatabase()).thenReturn(true);
        final CleanInMemoryDatabase cleanInMemoryDatabaseSpy = spy(cleanInMemoryDatabase);
        cleanInMemoryDatabaseSpy.delegateExecute(delegateExecution);
        verify(cleanInMemoryDatabaseSpy).updateDelegateStatus("Cleaning in memory db on Node abc was successful.");

    }
}
