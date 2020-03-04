package blockchain;

public class Controller implements ControllerInterface{
    private final ModelInterface model;
    private final ViewInterface view;

    public Controller(ModelInterface model, ViewInterface view) {
        this.model = model;
        this.view = view;
        start();
    }

    private void start() {
        if (model.deserializeAndContinue()) ;
        else model.startAnew(view.input());

        view.initialize(this, model);
    }
}
