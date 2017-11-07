# Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved
TEST_SUITE_NAME='DNE Multi-Node REST Endpoints Tests'
REPORT_NAME='dne_paqx_multinode_test_suite_report'
SEARCH_PATH=$AF_TEST_SUITE_PATH/dne_paqx_parent
MARKER='dne_paqx_multinode'

. $HOME/af_env.sh
py3clean .
export AF_TEST_SUITE_NAME=$TEST_SUITE_NAME
py.test $SEARCH_PATH -m "$MARKER" --html $AF_REPORTS_PATH/all/$REPORT_NAME.html --self-contained-html --json $AF_REPORTS_PATH/all/$REPORT_NAME.json --junit-xml $AF_REPORTS_PATH/all/$REPORT_NAME.xml
export AF_TEST_SUITE_NAME='Test Suite Name Not Set'
