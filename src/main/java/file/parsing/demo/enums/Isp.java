package file.parsing.demo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Isp {
    AWS_ISP("Amazon Data Services"),
    AWS_ORG("AWS"),
    GCP_ISP("Google"),
    GCP_ORG("Google Cloud"),
    AZURE_ISP("Microsoft Corporation"),
    AZURE_ORG("Microsoft Azure");

    public final String value;
}
