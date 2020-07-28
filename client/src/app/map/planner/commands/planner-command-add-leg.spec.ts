import {TestSupport} from "../../../util/test-support";
import {PlannerTestSetup} from "../context/planner-test-setup";
import {PlanFlag} from "../plan/plan-flag";
import {PlanUtil} from "../plan/plan-util";
import {PlannerCommandAddLeg} from "./planner-command-add-leg";
import {PlannerCommandAddStartPoint} from "./planner-command-add-start-point";

describe("PlannerCommandAddLeg", () => {

  it("do and undo", () => {

    const setup = new PlannerTestSetup();

    const startFlag = PlanFlag.start("startFlag", setup.node1.coordinate);
    const sinkFlag = PlanFlag.end("sinkFlag", setup.node2.coordinate);

    const leg = PlanUtil.singleRoutePlanLeg("12", setup.node1, setup.node2, sinkFlag, null);

    const addStartCommand = new PlannerCommandAddStartPoint(setup.node1, startFlag);
    setup.context.execute(addStartCommand);

    const command = new PlannerCommandAddLeg(leg);
    setup.context.execute(command);

    setup.markerLayer.expectFlagCount(2);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectEndFlagExists("sinkFlag", [2, 2]);
    setup.routeLayer.expectRouteLegCount(1);
    setup.routeLayer.expectRouteLegExists("12", leg);

    expect(setup.context.plan.legs.size).toEqual(1);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);

    command.undo(setup.context);

    setup.markerLayer.expectFlagCount(1);
    setup.routeLayer.expectRouteLegCount(0);
    expect(setup.context.plan.legs.size).toEqual(0);

    command.do(setup.context);

    setup.markerLayer.expectFlagCount(2);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectEndFlagExists("sinkFlag", [2, 2]);
    setup.routeLayer.expectRouteLegCount(1);
    setup.routeLayer.expectRouteLegExists("12", leg);

    expect(setup.context.plan.legs.size).toEqual(1);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);

  });

  it("previous end flag changes to via flag", () => {

    const setup = new PlannerTestSetup();

    const startFlag = PlanFlag.start("startFlag", [1, 1]);
    const sinkFlag1 = PlanFlag.end("sinkFlag1", [2, 2]);
    const sinkFlag2 = PlanFlag.end("sinkFlag2", [3, 3]);
    const sinkFlag3 = PlanFlag.end("sinkFlag3", [4, 4]);

    const leg1 = PlanUtil.singleRoutePlanLeg("12", setup.node1, setup.node2, sinkFlag1, null);
    const leg2 = PlanUtil.singleRoutePlanLeg("23", setup.node2, setup.node3, sinkFlag2, null);
    const leg3 = PlanUtil.singleRoutePlanLeg("34", setup.node3, setup.node4, sinkFlag3, null);

    const addStartCommand = new PlannerCommandAddStartPoint(setup.node1, startFlag);
    setup.context.execute(addStartCommand);

    setup.markerLayer.expectFlagCount(1);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);

    const addLeg1Command = new PlannerCommandAddLeg(leg1);
    setup.context.execute(addLeg1Command);
    setup.markerLayer.expectFlagCount(2);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectEndFlagExists("sinkFlag1", [2, 2]);
    setup.routeLayer.expectRouteLegExists("12", leg1);

    expect(setup.context.plan.legs.size).toEqual(1);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag1", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);

    const addLeg2Command = new PlannerCommandAddLeg(leg2);
    setup.context.execute(addLeg2Command);
    setup.markerLayer.expectFlagCount(3);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectViaFlagExists("sinkFlag1", [2, 2]);
    setup.markerLayer.expectEndFlagExists("sinkFlag2", [3, 3]);
    setup.routeLayer.expectRouteLegExists("12", leg1);
    setup.routeLayer.expectRouteLegExists("23", leg2);

    expect(setup.context.plan.legs.size).toEqual(2);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectViaFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag1", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);
    expect(setup.context.plan.legs.get(1).featureId).toEqual("23");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(1).sinkFlag, "sinkFlag2", [3, 3]);
    expect(setup.context.plan.legs.get(1).viaFlag).toEqual(null);

    const addLeg3Command = new PlannerCommandAddLeg(leg3);
    setup.context.execute(addLeg3Command);
    setup.markerLayer.expectFlagCount(4);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectViaFlagExists("sinkFlag1", [2, 2]);
    setup.markerLayer.expectViaFlagExists("sinkFlag2", [3, 3]);
    setup.markerLayer.expectEndFlagExists("sinkFlag3", [4, 4]);
    setup.routeLayer.expectRouteLegCount(3);
    setup.routeLayer.expectRouteLegExists("12", leg1);
    setup.routeLayer.expectRouteLegExists("23", leg2);
    setup.routeLayer.expectRouteLegExists("34", leg3);

    expect(setup.context.plan.legs.size).toEqual(3);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectViaFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag1", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);
    expect(setup.context.plan.legs.get(1).featureId).toEqual("23");
    TestSupport.expectViaFlag(setup.context.plan.legs.get(1).sinkFlag, "sinkFlag2", [3, 3]);
    expect(setup.context.plan.legs.get(1).viaFlag).toEqual(null);
    expect(setup.context.plan.legs.get(2).featureId).toEqual("34");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(2).sinkFlag, "sinkFlag3", [4, 4]);
    expect(setup.context.plan.legs.get(2).viaFlag).toEqual(null);

    addLeg3Command.undo(setup.context);
    setup.markerLayer.expectFlagCount(3);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectViaFlagExists("sinkFlag1", [2, 2]);
    setup.markerLayer.expectEndFlagExists("sinkFlag2", [3, 3]);
    setup.routeLayer.expectRouteLegCount(2);
    setup.routeLayer.expectRouteLegExists("12", leg1);
    setup.routeLayer.expectRouteLegExists("23", leg2);

    expect(setup.context.plan.legs.size).toEqual(2);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectViaFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag1", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);
    expect(setup.context.plan.legs.get(1).featureId).toEqual("23");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(1).sinkFlag, "sinkFlag2", [3, 3]);
    expect(setup.context.plan.legs.get(1).viaFlag).toEqual(null);

    addLeg2Command.undo(setup.context);
    setup.markerLayer.expectFlagCount(2);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectEndFlagExists("sinkFlag1", [2, 2]);
    setup.routeLayer.expectRouteLegCount(1);
    setup.routeLayer.expectRouteLegExists("12", leg1);

    expect(setup.context.plan.legs.size).toEqual(1);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag1", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);

    addLeg1Command.undo(setup.context);
    setup.markerLayer.expectFlagCount(1);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.routeLayer.expectRouteLegCount(0);

    addLeg1Command.do(setup.context); // redo

    setup.markerLayer.expectFlagCount(2);
    setup.markerLayer.expectStartFlagExists("startFlag", [1, 1]);
    setup.markerLayer.expectEndFlagExists("sinkFlag1", [2, 2]);
    setup.routeLayer.expectRouteLegCount(1);
    setup.routeLayer.expectRouteLegExists("12", leg1);

    expect(setup.context.plan.legs.size).toEqual(1);
    expect(setup.context.plan.legs.get(0).featureId).toEqual("12");
    TestSupport.expectEndFlag(setup.context.plan.legs.get(0).sinkFlag, "sinkFlag1", [2, 2]);
    expect(setup.context.plan.legs.get(0).viaFlag).toEqual(null);

    addLeg1Command.undo(setup.context);
    addStartCommand.undo(setup.context);
    setup.markerLayer.expectFlagCount(0);
    setup.routeLayer.expectRouteLegCount(0);

  });

});
