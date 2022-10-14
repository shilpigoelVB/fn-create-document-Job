/*
** ObjectStorageListObjects version 1.0.
**
** Copyright (c) 2020 Oracle, Inc.
** Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
*/

package com.example.fn;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.aivision.AIServiceVisionClient;
import com.oracle.bmc.aivision.model.*;
import com.oracle.bmc.aivision.requests.*;
import com.oracle.bmc.aivision.responses.*;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HelloFunction {

    private AIServiceVisionClient client = null;

    final ResourcePrincipalAuthenticationDetailsProvider provider
            = ResourcePrincipalAuthenticationDetailsProvider.builder().build();

    public HelloFunction() {
        try {

            //print env vars in Functions container
            System.err.println("OCI_RESOURCE_PRINCIPAL_VERSION " + System.getenv("OCI_RESOURCE_PRINCIPAL_VERSION"));
            System.err.println("OCI_RESOURCE_PRINCIPAL_REGION " + System.getenv("OCI_RESOURCE_PRINCIPAL_REGION"));
            System.err.println("OCI_RESOURCE_PRINCIPAL_RPST " + System.getenv("OCI_RESOURCE_PRINCIPAL_RPST"));
            System.err.println("OCI_RESOURCE_PRINCIPAL_PRIVATE_PEM " + System.getenv("OCI_RESOURCE_PRINCIPAL_PRIVATE_PEM"));

            client = new AIServiceVisionClient(provider);

        } catch (Throwable ex) {
            System.err.println("Failed to instantiate AIServiceVision client - " + ex.getMessage());
        }
    }

    public List<String> handleRequest(final String bucketName,final String outputBucketName, final String compId) {

        if (client == null) {
            System.err.println("There was a problem creating the AIServiceVision Client object. Please check logs");
            return Collections.emptyList();
        }

        List<String> objNames = null;
        try {
            String nameSpace = System.getenv().get("NAMESPACE");
            System.err.println("before calling createDocumentJob in namespace "+nameSpace);
            /* Create a request and dependent object(s). */
            CreateDocumentJobDetails createDocumentJobDetails = CreateDocumentJobDetails.builder()
            .inputLocation(ObjectListInlineInputLocation.builder()
                .objectLocations(new ArrayList<>(Arrays.asList(ObjectLocation.builder()
                        .namespaceName(nameSpace)
                        .bucketName(bucketName)
                        .objectName("passport.pdf").build()))).build())
            .features(new ArrayList<>(Arrays.asList(DocumentKeyValueDetectionFeature.builder().build())))
            .outputLocation(OutputLocation.builder()
                .namespaceName(nameSpace)
                .bucketName(outputBucketName)
                .prefix("EXAMPLE-prefix-Value").build())
            .compartmentId(compId)
            .displayName("EXAMPLE-displayName-Value")
            .language(DocumentLanguage.Spa)
            .documentType(DocumentType.Payslip)
            .isZipOutputEnabled(true).build();
    
                
                    CreateDocumentJobRequest createDocumentJobRequest = CreateDocumentJobRequest.builder()
                        .createDocumentJobDetails(createDocumentJobDetails)
                        .build();
        	/* Send request to the Client */
            CreateDocumentJobResponse response = client.createDocumentJob(createDocumentJobRequest);

        } catch (Throwable e) {
            System.err.println("Error fetching object list from bucket " + e.getMessage());
        }

        return Collections.emptyList();
    }
}
